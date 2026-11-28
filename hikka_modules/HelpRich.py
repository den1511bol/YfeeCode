# meta developer: @yfeeeee
# scope: hikka_only
# scope: hikka_min 1.6.0
#
# HelpRich: подменяет обычную команду .help. Каждый модуль отдельным свёрнутым списком
# (<details>), внутри его команды с описаниями. Отдельная команда не нужна: пишешь .help
# как обычно. С аргументом (.help имя) работает оригинальный help.
# При выгрузке модуля оригинальный help возвращается.
# Автор: @yfeeeee

import html as _html
import inspect
import logging

from .. import loader, utils

logger = logging.getLogger(__name__)

LIMIT = 3000  # знаков на одно сообщение, длинное Hikka превращает в инлайн-форму


def esc(text):
    return _html.escape(str(text), quote=False)


def mod_name(mod):
    return getattr(mod, "strings", {}).get("name", mod.__class__.__name__)


def mod_commands(mod, prefix):
    res = []
    for name, func in sorted((getattr(mod, "commands", None) or {}).items()):
        doc = (getattr(func, "__doc__", "") or "").strip().split("\n")[0]
        res.append((prefix + name, doc))
    return res


@loader.tds
class HelpRichMod(loader.Module):
    """Заменяет .help: каждый модуль свёрнутым списком. Автор: @yfeeeee"""

    strings = {"name": "HelpRich"}

    def __init__(self):
        self.config = loader.ModuleConfig(
            loader.ConfigValue(
                "mode",
                "details",
                "details (<details>) или quote (свёрнутая цитата, запасной вариант)",
                validator=loader.validators.Choice(["details", "quote"]),
            ),
            loader.ConfigValue(
                "show_empty",
                False,
                "Показывать модули без команд",
                validator=loader.validators.Boolean(),
            ),
        )
        self._orig = None
        self._disp = None

    def _install(self):
        """Подмена help сразу в трёх местах: словарь команд, модуль Help и dispatch."""
        am = self.allmodules
        try:
            cmds = am.commands
            cur = cmds.get("help")
            if cur is not None and getattr(cur, "__self__", None) is not self:
                self._orig = cur
            cmds["help"] = self._help
        except Exception as e:
            logger.warning("HelpRich commands: %s", e)
        for mod in am.modules:
            if mod is self:
                continue
            mc = getattr(mod, "commands", None)
            if isinstance(mc, dict) and "help" in mc:
                try:
                    if self._orig is None:
                        self._orig = mc["help"]
                    mc["help"] = self._help
                except Exception as e:
                    logger.warning("HelpRich module commands: %s", e)
        disp = getattr(am, "dispatch", None)
        if disp is not None and not getattr(disp, "_helprich", False) and not inspect.iscoroutinefunction(disp):
            outer = self

            def dispatch(command, *a, **kw):
                res = disp(command, *a, **kw)
                try:
                    if isinstance(res, tuple) and len(res) == 2 and str(res[0]).lower() == "help":
                        return res[0], outer._help
                except Exception:
                    pass
                return res

            dispatch._helprich = True
            dispatch._orig = disp
            self._disp = disp
            am.dispatch = dispatch

    def _uninstall(self):
        am = self.allmodules
        if self._orig is not None:
            try:
                am.commands["help"] = self._orig
            except Exception:
                pass
            for mod in am.modules:
                mc = getattr(mod, "commands", None)
                if isinstance(mc, dict) and mc.get("help") == self._help:
                    mc["help"] = self._orig
        if getattr(self, "_disp", None) is not None:
            am.dispatch = self._disp
            self._disp = None

    async def client_ready(self, client, db):
        self._client = client
        self._install()

    async def on_unload(self):
        self._uninstall()

    def _blocks(self):
        prefix = self.get_prefix()
        quote = self.config["mode"] == "quote"
        out = []
        for mod in self.allmodules.modules:
            if mod is self:
                continue
            name = esc(mod_name(mod))
            cmds = mod_commands(mod, prefix)
            if not cmds:
                if self.config["show_empty"]:
                    out.append("<b>📦 %s</b>" % name if quote else "<p><b>📦 %s</b></p>" % name)
                continue
            if quote:
                lines = ["<code>%s</code> - %s" % (esc(c), esc(d)) if d else "<code>%s</code>" % esc(c) for c, d in cmds]
                out.append("<b>📦 %s</b>\n<blockquote expandable>%s</blockquote>" % (name, "\n".join(lines)))
            else:
                body = "".join(
                    "<p><code>%s</code>%s</p>" % (esc(c), (" - " + esc(d)) if d else "") for c, d in cmds
                )
                out.append("<details><summary>📦 %s (%d)</summary>%s</details>" % (name, len(cmds), body))
        return out

    async def _help(self, message):
        """Список модулей и команд (подменён HelpRich)"""
        args = utils.get_args_raw(message)
        if args and self._orig is not None:
            return await self._orig(message)
        blocks = self._blocks()
        sep = "\n" if self.config["mode"] == "quote" else ""
        chunks, cur = [], ""
        for b in blocks:
            if cur and len(cur) + len(b) > LIMIT:
                chunks.append(cur)
                cur = ""
            cur += b + sep
        if cur:
            chunks.append(cur)
        if not chunks:
            chunks = ["<b>Модулей нет</b>"]
        head = "<b>📚 Модулей: %d</b>\n" % len(blocks)
        await utils.answer(message, head + chunks[0])
        for ch in chunks[1:]:
            await self._client.send_message(utils.get_chat_id(message), ch, parse_mode="html")

    async def helprichcmd(self, message):
        """Вернуть оригинальный help (.helprich off) или включить снова (.helprich on)"""
        arg = utils.get_args_raw(message).strip().lower()
        if arg == "off":
            self._uninstall()
            await utils.answer(message, "Оригинальный help возвращён")
        else:
            self._install()
            await utils.answer(message, "HelpRich включён")
