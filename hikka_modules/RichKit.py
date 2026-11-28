# meta developer: @yfeeeee
# scope: hikka_only
# scope: hikka_min 1.6.0
#
# RichKit: сворачиваемые списки (details/summary) для Hikka и Heroku-юзербота.
# Автор: @yfeeeee
#
# Как это устроено (по реальному модулю Always Online, который прислал пользователь):
#   юзербот шлёт обычный HTML через utils.answer, а внутри есть <details><summary>...</summary>...</details>
#   и абзацы <p>. Telegram показывает это сворачиваемым блоком. Сработает, если парсер
#   вашего юзербота понимает <details>. Не проверено на всех версиях.
#
# Режимы (настройка mode):
#   quote:   <blockquote expandable> (по умолчанию), у автора показалась без сбоев
#   details: <details><summary>, у автора в юзерботе теги пропали, текст склеился
#   bot:     rich-сообщение через ассистент-бота (sendRichMessage), нужен бот в чате
# Настройка tables: вместо строк-абзацев команды выводятся таблицей <table> (не проверено с аккаунта).

import html as _html
import logging

import aiohttp

from .. import loader, utils

logger = logging.getLogger(__name__)


def esc(text):
    return _html.escape(str(text), quote=False)


def details(summary, body_html, open_=False):
    """Сворачиваемый блок. summary это заголовок, body_html это HTML внутри."""
    o = " open" if open_ else ""
    return "<details%s><summary>%s</summary>%s</details>" % (o, summary, body_html)


def table(rows, header=None):
    """Таблица. rows это список строк, строка это список готового HTML ячеек."""
    out = ["<table bordered striped compact>"]
    if header:
        out.append("<tr>" + "".join("<th>%s</th>" % h for h in header) + "</tr>")
    for r in rows:
        out.append("<tr>" + "".join('<td valign="middle">%s</td>' % c for c in r) + "</tr>")
    out.append("</table>")
    return "".join(out)


def para(html_text):
    return "<p>%s</p>" % html_text


def cmd_lines(cmds):
    """Команды абзацами: <p><code>.cmd</code> - описание</p>"""
    out = []
    for c, d in cmds:
        line = "<code>%s</code>" % esc(c)
        if d:
            line += " - " + esc(d)
        out.append(para(line))
    return "".join(out)


def cmd_table(cmds, header=None):
    rows = [["<code>%s</code>" % esc(c), esc(d)] for c, d in cmds]
    return table(rows, header)


def fallback_list(title, lines):
    """Свёрнутая цитата: заголовок и список строк внутри <blockquote expandable>."""
    return "<b>%s</b>\n<blockquote expandable>%s</blockquote>" % (title, "\n".join(lines))


def fallback_sections(sections):
    """Несколько блоков: у каждого заголовок и своя свёрнутая цитата."""
    out = []
    for title, lines in sections:
        out.append(fallback_list(title, lines) if lines else "<b>%s</b>" % title)
    return "\n".join(out)


def module_commands(mod, prefix):
    """Список (команда, описание) модуля Hikka."""
    res = []
    cmds = getattr(mod, "commands", None) or {}
    for name, func in sorted(cmds.items()):
        doc = (getattr(func, "__doc__", "") or "").strip().split("\n")[0]
        res.append((prefix + name, doc))
    return res


def module_name(mod):
    return getattr(mod, "strings", {}).get("name", mod.__class__.__name__)


@loader.tds
class RichKitMod(loader.Module):
    """Сворачиваемые списки (details) для вывода модулей и команд. Автор: @yfeeeee"""

    strings = {
        "name": "RichKit",
        "no_mod": "<b>Модуль не найден.</b>",
        "ask_mod": "<b>Укажи модуль:</b> <code>%srhelp имя</code>",
    }

    def __init__(self):
        self.config = loader.ModuleConfig(
            loader.ConfigValue(
                "mode",
                "quote",
                "Режим: quote (свёрнутая цитата), details (<details>, у автора теги пропали) или bot",
                validator=loader.validators.Choice(["quote", "details", "bot"]),
            ),
            loader.ConfigValue(
                "tables",
                False,
                "Команды таблицей вместо строк (таблицы с аккаунта не проверены)",
                validator=loader.validators.Boolean(),
            ),
        )

    async def _send_rich(self, chat_id, html_text):
        """Прямой вызов Bot API sendRichMessage через ассистент-бота. True если вышло."""
        try:
            token = self.inline.bot.token
        except Exception:
            return False
        url = "https://api.telegram.org/bot%s/sendRichMessage" % token
        payload = {"chat_id": chat_id, "rich_message": {"html": html_text}}
        try:
            async with aiohttp.ClientSession() as s:
                async with s.post(url, json=payload, timeout=aiohttp.ClientTimeout(total=20)) as r:
                    data = await r.json()
            if not data.get("ok"):
                logger.warning("sendRichMessage: %s", data.get("description"))
            return bool(data.get("ok"))
        except Exception as e:
            logger.warning("sendRichMessage error: %s", e)
            return False

    async def _show(self, message, rich_html, fb_html):
        """Показывает по режиму; при сбое падает на свёрнутую цитату."""
        mode = self.config["mode"]
        if mode == "details":
            try:
                await utils.answer(message, rich_html)
                return
            except Exception as e:
                logger.warning("details failed: %s", e)
        elif mode == "bot":
            if await self._send_rich(utils.get_chat_id(message), rich_html):
                try:
                    await message.delete()
                except Exception:
                    pass
                return
        await utils.answer(message, fb_html)

    def _body(self, cmds, header=None):
        return cmd_table(cmds, header) if self.config["tables"] else cmd_lines(cmds)

    async def richcmd(self, message):
        """Тест: закрытый и открытый свёрнутые списки"""
        rich = (
            para("<b>RichKit</b> <code>(by @yfeeeee)</code>")
            + details("Закрытый список", para("Скрытый текст"))
            + details("Открытый список", para("Видно сразу"), True)
        )
        await self._show(message, rich, fallback_list("RichKit", ["Автор: @yfeeeee", "Свёрнутый список работает"]))

    async def rmodscmd(self, message):
        """Список модулей: каждый свёрнут, внутри его команды"""
        prefix = self.get_prefix()
        parts = []
        sections = []
        for mod in self.allmodules.modules:
            name = esc(module_name(mod))
            cmds = module_commands(mod, prefix)
            if cmds:
                parts.append(details("📦 " + name, self._body(cmds)))
            else:
                parts.append(para("<b>📦 %s</b>" % name))
            sections.append(("📦 " + name, ["%s - %s" % (esc(c), esc(d)) if d else esc(c) for c, d in cmds]))
        await self._show(message, "".join(parts), fallback_sections(sections))

    async def rhelpcmd(self, message):
        """<модуль> - команды модуля в свёрнутом блоке"""
        args = utils.get_args_raw(message).strip().lower()
        prefix = self.get_prefix()
        if not args:
            await utils.answer(message, self.strings["ask_mod"] % prefix)
            return
        for mod in self.allmodules.modules:
            name = module_name(mod)
            if name.lower() == args:
                cmds = module_commands(mod, prefix)
                rich = para("<b>%s</b>" % esc(name)) + details("Commands", self._body(cmds, ["Команда", "Описание"]), True)
                lines = ["%s - %s" % (esc(c), esc(d)) if d else esc(c) for c, d in cmds]
                await self._show(message, rich, fallback_list("📦 " + esc(name), lines))
                return
        await utils.answer(message, self.strings["no_mod"])
