# YfeeCode — developer: @yfeeeee
"""Offline sandbox for Heroku/Hikka-style modules.

Provides fake `loader`, `utils` and a mock message so a module's commands
can be called without Telegram. This is a best-effort shim, not the real
framework: Telegram API calls (self.client) are stubbed.
"""
import importlib.util
import inspect
import sys
import types
import asyncio
import traceback


class _Strings(dict):
    def __missing__(self, key):
        return key

    def __call__(self, key):
        return self[key]


class Module:
    strings = {}
    strings_ru = {}

    def get(self, k, default=None):
        return self.__dict__.setdefault("_db", {}).get(k, default)

    def set(self, k, v):
        self.__dict__.setdefault("_db", {})[k] = v

    @property
    def client(self):
        return self.__dict__.setdefault("_client", _FakeClient())

    @property
    def _client_db(self):
        return self.__dict__.setdefault("_db", {})

    def config_complete(self):
        pass


class ConfigValue:
    def __init__(self, option, default=None, doc=None, validator=None, **kw):
        self.option = option
        self.default = default
        self.doc = doc


class ModuleConfig(dict):
    def __init__(self, *items):
        super().__init__()
        for it in items:
            if isinstance(it, ConfigValue):
                self[it.option] = it.default
        # also support the old flat form: ModuleConfig("name", default, "doc", ...)
        if items and not isinstance(items[0], ConfigValue):
            for i in range(0, len(items) - 1, 3):
                self[items[i]] = items[i + 1]


class _FakeClient:
    async def send_message(self, chat, text, **kw):
        print("[send_message] %s: %s" % (chat, text))

    def __getattr__(self, name):
        async def stub(*a, **k):
            print("[client.%s] stubbed" % name)
        return stub


def tds(cls):
    return cls


def _deco(*a, **k):
    if len(a) == 1 and callable(a[0]) and not k:
        a[0]._is_cmd = True
        return a[0]

    def inner(f):
        f._is_cmd = True
        return f
    return inner


class Message:
    def __init__(self, text):
        self.text = text
        self.raw_text = text
        self.chat_id = 1
        self.sender_id = 1
        self.out = True

    async def edit(self, text, **kw):
        print("[edit] " + str(text))
        return self

    async def respond(self, text="", **kw):
        print("[respond] " + str(text))
        return self

    async def reply(self, text="", **kw):
        print("[reply] " + str(text))
        return self

    async def delete(self):
        print("[delete]")

    async def get_reply_message(self):
        return None


def get_args_raw(m):
    parts = (m.text or "").split(maxsplit=1)
    return parts[1] if len(parts) > 1 else ""


def get_args(m):
    return get_args_raw(m).split()


async def answer(m, text, **kw):
    return await m.edit(text)


def _install_shims():
    loader = types.ModuleType("loader")
    loader.Module = Module
    loader.ModuleConfig = ModuleConfig
    loader.tds = tds
    loader.command = _deco
    loader.owner = _deco
    loader.unrestricted = _deco
    loader.watcher = _deco
    loader.loop = _deco
    loader.ConfigValue = ConfigValue
    loader.validators = types.SimpleNamespace(
        String=lambda *a, **k: None, Integer=lambda *a, **k: None,
        Boolean=lambda *a, **k: None, Series=lambda *a, **k: None)
    utils = types.ModuleType("utils")
    utils.get_args_raw = get_args_raw
    utils.get_args = get_args
    utils.answer = answer
    utils.escape_html = lambda t: str(t).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    utils.get_chat_id = lambda m: getattr(m, "chat_id", 1)
    pkg = types.ModuleType("shimpkg")
    pkg.loader = loader
    pkg.utils = utils
    pkg.__path__ = []
    sys.modules["shimpkg"] = pkg
    sys.modules["shimpkg.loader"] = loader
    sys.modules["shimpkg.utils"] = utils
    for name in ("herokutl", "hikkatl"):
        sys.modules.setdefault(name, types.ModuleType(name))


def _load(path):
    _install_shims()
    src = open(path, encoding="utf-8").read()
    # relative imports of the framework -> shim package
    src = src.replace("from .. import loader, utils", "from shimpkg import loader, utils")
    src = src.replace("from .. import utils, loader", "from shimpkg import loader, utils")
    src = src.replace("from .. import loader", "from shimpkg import loader")
    src = src.replace("from .. import utils", "from shimpkg import utils")
    mod = types.ModuleType("user_module")
    mod.__file__ = path
    exec(compile(src, path, "exec"), mod.__dict__)
    for _, obj in vars(mod).items():
        if inspect.isclass(obj) and issubclass(obj, Module) and obj is not Module:
            return obj
    raise RuntimeError("No class inherited from loader.Module found")


def commands(path):
    cls = _load(path)
    out = []
    for n, f in inspect.getmembers(cls, inspect.isfunction):
        if getattr(f, "_is_cmd", False) or n.endswith("cmd"):
            out.append(n[:-3] if n.endswith("cmd") else n)
    return out


def call(path, line):
    """line like '.hello world'. Returns captured output."""
    import io
    import contextlib
    buf = io.StringIO()
    with contextlib.redirect_stdout(buf):
        try:
            cls = _load(path)
            inst = cls()
            inst.strings = _Strings(getattr(cls, "strings", {}))
            ready = getattr(inst, "client_ready", None)
            if ready:
                r = ready(inst.client, inst.__dict__.setdefault("_db", {}))
                if inspect.iscoroutine(r):
                    asyncio.run(r)
            text = line.lstrip(".")
            name = text.split()[0] if text.split() else ""
            f = getattr(inst, name + "cmd", None) or getattr(inst, name, None)
            if f is None:
                print("No command: " + name)
            else:
                res = f(Message("." + text))
                if inspect.iscoroutine(res):
                    asyncio.run(res)
        except BaseException:
            traceback.print_exc()
    return buf.getvalue()
