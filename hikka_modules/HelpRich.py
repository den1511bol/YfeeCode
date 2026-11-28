# meta developer: @yfeeeee
# scope: hikka_only
# scope: hikka_min 1.6.0
#
# HelpRich: подменяет обычную команду .help. Каждый модуль отдельным свёрнутым списком
# (<details>), внутри его команды с описаниями. Отдельная команда не нужна: пишешь .help
# как обычно. .help имя показывает команды одного модуля в том же стиле.
# Баннер: ссылка на картинку/гифку из banner_url (настройки HelpRich или Help), ставится сверху в цитате.
# Премиум-эмодзи берутся из настроек модуля Help (plain_emoji, core_emoji, command_emoji, desc_icon),
# либо задай свои в настройках HelpRich (.fcfg HelpRich).
# Сделано под Heroku (coddrago/Heroku): обёртка над dispatch_candidates.
# При выгрузке модуля оригинальный help возвращается.
# Автор: @yfeeeee

import html as _html
import logging
import random
import re

from .. import loader, utils

logger = logging.getLogger(__name__)

LIMIT = 3000  # знаков на одно сообщение, длинное Hikka превращает в инлайн-форму


def esc(text):
    return _html.escape(str(text), quote=False)


_EMOJI_RE = re.compile(r"<emoji\s+document_id=[\"']?(\d+)[\"']?\s*>(.*?)</emoji\s*>?", re.S)


def fix_emoji(val):
    """<emoji document_id=ID>X</emoji> превращает в <tg-emoji emoji-id=\"ID\">X</tg-emoji>."""
    return _EMOJI_RE.sub(lambda m: '<tg-emoji emoji-id="%s">%s</tg-emoji>' % (m.group(1), m.group(2)), str(val or ""))


_TG_RE = re.compile(r"<tg-emoji[^>]*>(.*?)</tg-emoji>", re.S)


def plain_emoji(html_text):
    """Убирает премиум-эмодзи, оставляя обычный символ (в summary Telegram их не принимает)."""
    return _TG_RE.sub(lambda m: m.group(1), html_text)


_ANY_EMOJI = re.compile("[\U0001F000-\U0001FAFF\u2190-\u21FF\u2300-\u23FF\u2600-\u27BF\u2B00-\u2BFF\uFE0F\u200D]")


def no_emoji(html_text):
    """Совсем без эмодзи: последний запасной вариант rich-сообщения."""
    return _ANY_EMOJI.sub("", plain_emoji(html_text))


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
                "nested",
                False,
                "Список внутри списка: все модули внутри одного свёрнутого блока (если Telegram покажет вложенность)",
                validator=loader.validators.Boolean(),
            ),
            loader.ConfigValue(
                "plain_emoji",
                "",
                "Эмодзи перед модулем. Пусто: берётся из настроек модуля Help (.fcfg help plain_emoji)",
            ),
            loader.ConfigValue(
                "core_emoji",
                "",
                "Эмодзи перед встроенным модулем. Пусто: берётся из модуля Help",
            ),
            loader.ConfigValue(
                "command_emoji",
                "•",
                "Значок перед командой (по умолчанию точка). Можно вставить эмодзи",
            ),
            loader.ConfigValue(
                "desc_icon",
                "",
                "Эмодзи в заголовке. Пусто: берётся из модуля Help",
            ),
            loader.ConfigValue(
                "banner_url",
                "",
                "Ссылка на картинку или гифку сверху в цитате. Пусто: берётся banner_url из модуля Help",
            ),
            loader.ConfigValue(
                "show_empty",
                False,
                "Показывать модули без команд",
                validator=loader.validators.Boolean(),
            ),
        )
        self._orig = None
        self._cand = None
        self._hidden_count = 0

    def _install(self):
        """Heroku берёт обработчик из dispatch_candidates (последний в списке).
        Оборачиваем его: для команды help подставляем только свой обработчик (иначе Heroku спросит, какой модуль выбрать).
        Словарь commands не трогаем, его раз в 30 секунд пересобирает сам юзербот."""
        am = self.allmodules
        cur = getattr(am, "dispatch_candidates", None)
        if cur is None or getattr(cur, "_helprich", False):
            return
        outer = self

        def dispatch_candidates(command, *a, **kw):
            res = cur(command, *a, **kw)
            try:
                txt, hs = res
                if hs and str(txt).split()[0].lower() == "help":
                    last = hs[-1]
                    if getattr(last, "__self__", None) is not outer:
                        outer._orig = last
                    # один обработчик: если их несколько, Heroku показывает меню выбора модуля
                    return txt, [outer._help]
            except Exception as e:
                logger.warning("HelpRich dispatch: %s", e)
            return res

        dispatch_candidates._helprich = True
        self._cand = cur
        am.dispatch_candidates = dispatch_candidates

    def _uninstall(self):
        if getattr(self, "_cand", None) is not None:
            self.allmodules.dispatch_candidates = self._cand
            self._cand = None

    async def client_ready(self, client, db):
        self._client = client
        self._install()

    async def on_unload(self):
        self._uninstall()

    def _em(self, key, fallback):
        """Эмодзи: своя настройка, иначе настройка модуля Help (премиум-эмодзи), иначе запасной."""
        val = self.config[key]
        if not val:
            for m in self.allmodules.modules:
                if getattr(m, "strings", {}).get("name") == "Help":
                    try:
                        val = m.config[key]
                    except Exception:
                        val = ""
                    break
        return fix_emoji(val) or fallback

    def _banner(self):
        """Своя ссылка из настроек HelpRich или banner_url из модуля Help (там может быть список)."""
        url = self.config["banner_url"]
        if not url:
            for m in self.allmodules.modules:
                if getattr(m, "strings", {}).get("name") == "Help":
                    try:
                        url = m.config["banner_url"]
                    except Exception:
                        url = ""
                    break
        if isinstance(url, (list, tuple)):
            url = random.choice(list(url)) if url else ""
        return str(url or "").strip()

    def _hidden(self):
        """Список скрытых модулей из базы Help (команда .helphide), имена классов."""
        for m in self.allmodules.modules:
            if getattr(m, "strings", {}).get("name") == "Help":
                try:
                    return list(m.get("hide", []))
                except Exception:
                    return []
        return []

    def _blocks(self, quote, force=False, only=None):
        """force: показать и скрытые (.help -f). only: core (.help -c) или loaded (.help -l)."""
        prefix = self.get_prefix()
        hidden = self._hidden()
        self._hidden_count = 0
        out = []
        for mod in self.allmodules.modules:
            if mod is self:
                continue
            core = str(getattr(mod, "__origin__", "")).startswith("<core")
            if only == "core" and not core:
                continue
            if only == "loaded" and core:
                continue
            if mod.__class__.__name__ in hidden and not force:
                self._hidden_count += 1
                continue
            name = esc(mod_name(mod))
            em = self._em("core_emoji" if core else "plain_emoji", "📦")
            cmds = mod_commands(mod, prefix)
            if not cmds:
                if self.config["show_empty"]:
                    out.append("<b>%s %s</b>" % (em, name) if quote else "<p><b>%s %s</b></p>" % (em, name))
                continue
            if quote:
                lines = ["<code>%s</code> - %s" % (esc(c), esc(d)) if d else "<code>%s</code>" % esc(c) for c, d in cmds]
                out.append("<b>%s %s</b>\n<blockquote expandable>%s</blockquote>" % (em, name, "\n".join(lines)))
            else:
                cem = fix_emoji(self.config["command_emoji"])
                body = "".join(
                    "<p>%s<code>%s</code>%s</p>" % ((cem + " ") if cem else "", esc(c), (" - " + esc(d)) if d else "")
                    for c, d in cmds
                )
                # премиум-эмодзи и в заголовке; если Telegram откажет, _help повторит с обычными
                out.append("<details><summary>%s %s</summary>%s</details>" % (em, name, body))
        return out

    def _find_module(self, arg):
        key = arg.strip().lower()
        for mod in self.allmodules.modules:
            if mod is self:
                continue
            if mod_name(mod).lower() == key or mod.__class__.__name__.lower() in (key, key + "mod"):
                return mod
        return None

    async def _module_help(self, message, mod):
        """.help имя: команды одного модуля в том же стиле."""
        prefix = self.get_prefix()
        cmd_em = fix_emoji(self.config["command_emoji"]) or "•"
        head_em = self._em("desc_icon", "🪐")
        name = esc(mod_name(mod))
        cmds = mod_commands(mod, prefix)
        lines = []
        for c, d in cmds:
            lines.append("<p>%s <code>%s</code>%s</p>" % (cmd_em, esc(c), (" - " + esc(d)) if d else ""))
        doc = (getattr(mod, "__doc__", "") or "").strip().split("\n")[0]
        rich = "<p>%s <b>%s</b></p>" % (head_em, name)
        if doc:
            rich += "<p>%s</p>" % esc(doc)
        if lines:
            rich += "<details><summary>Команды</summary>%s</details>" % "".join(lines)
        else:
            rich += "<p>Команд нет</p>"
        fn = getattr(utils, "answer_with_media_fallback", None)
        if fn is not None and self.config["mode"] == "details":
            for n, variant in enumerate((rich, plain_emoji(rich), no_emoji(rich))):
                try:
                    await fn(message, rich_message=variant)
                    return
                except Exception as e:
                    logger.warning("HelpRich module help variant %d failed: %s. Начало: %r", n + 1, e, variant[:400])
        q = ["%s <b>%s</b>" % (head_em, name)]
        q.append("<blockquote expandable>%s</blockquote>" % "\n".join(
            "%s <code>%s</code>%s" % (cmd_em, esc(c), (" - " + esc(d)) if d else "") for c, d in cmds))
        await utils.answer(message, "\n".join(q))

    async def _help(self, message):
        """Список модулей и команд (подменён HelpRich)"""
        args = utils.get_args_raw(message)
        force = "-f" in args or "-c" in args or "-l" in args
        only = "core" if "-c" in args else ("loaded" if "-l" in args else None)
        args = args.replace("-f", "").replace("-c", "").replace("-l", "").strip()
        if args:
            mod = self._find_module(args.split()[0])
            if mod is not None:
                return await self._module_help(message, mod)
            if self._orig is not None:
                return await self._orig(message)
        # Так отправляет сам Help при rich_mode: одним rich-сообщением
        if self.config["mode"] == "details":
            blocks = self._blocks(False, force, only)
            hid = (", скрыто: %d" % self._hidden_count) if self._hidden_count else ""
            head = "<p>%s <b>Модулей: %d%s</b></p>" % (self._em("desc_icon", "📚"), len(blocks), hid)
            if self.config["nested"]:
                blocks = ["<details><summary>Модули (%d)</summary>%s</details>" % (len(blocks), "".join(blocks))]
            rich = getattr(utils, "answer_with_media_fallback", None)
            if rich is not None:
                body = head + "".join(blocks)
                url = self._banner()
                tops = [""]
                if url:
                    img = '<figure><img src="%s"/></figure>' % esc(url).replace('"', "&quot;")
                    tops = ["<blockquote>%s</blockquote>" % img, img, ""]
                for t, top in enumerate(tops):
                    text = top + body
                    for n, variant in enumerate((text, plain_emoji(text), no_emoji(text))):
                        try:
                            await rich(message, rich_message=variant)
                            if n or t:
                                logger.warning("HelpRich: rich ушёл в варианте баннера %d, эмодзи %d", t + 1, n + 1)
                            return
                        except Exception as e:
                            logger.warning("HelpRich rich баннер %d эмодзи %d: %s. Начало: %r", t + 1, n + 1, e, variant[:300])
        await self._plain(message, self._blocks(True, force, only))

    async def _plain(self, message, blocks):
        """Запасной путь: свёрнутые цитаты обычными сообщениями до LIMIT знаков."""
        chunks, cur = [], ""
        for b in blocks:
            if cur and len(cur) + len(b) > LIMIT:
                chunks.append(cur)
                cur = ""
            cur += b + "\n"
        if cur:
            chunks.append(cur)
        if not chunks:
            chunks = ["<b>Модулей нет</b>"]
        hid = (", скрыто: %d" % self._hidden_count) if self._hidden_count else ""
        await utils.answer(message, "<b>📚 Модулей: %d%s</b>\n" % (len(blocks), hid) + chunks[0])
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
