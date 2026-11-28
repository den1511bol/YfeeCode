# meta developer: @yfeeeee
# scope: hikka_only
# scope: hikka_min 1.6.0
#
# HelpRich: подменяет обычную команду .help. Каждый модуль отдельным свёрнутым списком
# (<details>), внутри его команды с описаниями. Отдельная команда не нужна: пишешь .help
# как обычно. .help имя показывает команды одного модуля в том же стиле.
# Премиум-эмодзи берутся из настроек модуля Help (plain_emoji, core_emoji, command_emoji, desc_icon),
# либо задай свои в настройках HelpRich (.fcfg HelpRich).
# Сделано под Heroku (coddrago/Heroku): обёртка над dispatch_candidates.
# При выгрузке модуля оригинальный help возвращается.
# Автор: @yfeeeee

import html as _html
import logging
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
                "show_empty",
                False,
                "Показывать модули без команд",
                validator=loader.validators.Boolean(),
            ),
        )
        self._orig = None
        self._cand = None

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

    def _blocks(self, quote):
        prefix = self.get_prefix()
        out = []
        for mod in self.allmodules.modules:
            if mod is self:
                continue
            name = esc(mod_name(mod))
            core = str(getattr(mod, "__origin__", "")).startswith("<core")
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
                out.append("<details><summary>%s %s (%d)</summary>%s</details>" % (em, name, len(cmds), body))
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
            rich += "<details><summary>Команды (%d)</summary>%s</details>" % (len(cmds), "".join(lines))
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
        if args:
            mod = self._find_module(args.split()[0])
            if mod is not None:
                return await self._module_help(message, mod)
            if self._orig is not None:
                return await self._orig(message)
        # Так отправляет сам Help при rich_mode: одним rich-сообщением
        if self.config["mode"] == "details":
            blocks = self._blocks(False)
            head = "<p>%s <b>Модулей: %d</b></p>" % (self._em("desc_icon", "📚"), len(blocks))
            if self.config["nested"]:
                blocks = ["<details><summary>Модули (%d)</summary>%s</details>" % (len(blocks), "".join(blocks))]
            rich = getattr(utils, "answer_with_media_fallback", None)
            if rich is not None:
                text = head + "".join(blocks)
                for n, variant in enumerate((text, plain_emoji(text), no_emoji(text))):
                    try:
                        await rich(message, rich_message=variant)
                        if n:
                            logger.warning("HelpRich: rich ушёл только в варианте %d (1 премиум, 2 обычные эмодзи, 3 без эмодзи)", n + 1)
                        return
                    except Exception as e:
                        logger.warning("HelpRich rich variant %d failed: %s. Начало текста: %r", n + 1, e, variant[:400])
        await self._plain(message, self._blocks(True))

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
        await utils.answer(message, "<b>📚 Модулей: %d</b>\n" % len(blocks) + chunks[0])
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
