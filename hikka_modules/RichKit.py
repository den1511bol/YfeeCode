# meta developer: @yfeeeee
# scope: hikka_only
# scope: hikka_min 1.6.0
#
# RichKit: сворачиваемые списки (details/summary) и таблицы (table) для Hikka.
# Автор: @yfeeeee
#
# ВАЖНО (проверено только по документации Telegram, не на живом Hikka):
#   details и table это Rich Messages из Bot API 10.1+ (метод sendRichMessage, поле html).
#   Их шлёт бот, а не обычный аккаунт. Поэтому модуль шлёт через ассистент-бота Hikka.
#   Бот должен быть в этом чате (например, личка с ботом или группа, куда он добавлен).
#   Если отправка не вышла, модуль сам переходит на запасной вариант:
#   <blockquote expandable> (сворачиваемая цитата), которая работает и с аккаунта юзербота.

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


def fallback_list(title, lines):
    """Запасной вариант без rich: заголовок и сворачиваемая цитата."""
    body = "\n".join(lines)
    return "<b>%s</b>\n<blockquote expandable>%s</blockquote>" % (title, body)


def module_commands(mod, prefix):
    """Список (команда, описание) модуля Hikka."""
    res = []
    cmds = getattr(mod, "commands", None) or {}
    for name, func in sorted(cmds.items()):
        doc = (getattr(func, "__doc__", "") or "").strip().split("\n")[0]
        res.append((prefix + name, doc))
    return res


@loader.tds
class RichKitMod(loader.Module):
    """Сворачиваемые списки и таблицы (Rich Messages). Автор: @yfeeeee"""

    strings = {
        "name": "RichKit",
        "no_mod": "<b>Модуль не найден.</b>",
        "fb_note": "\n<i>Rich-отправка не удалась, показан запасной вариант.</i>",
    }

    def __init__(self):
        self.config = loader.ModuleConfig(
            loader.ConfigValue(
                "use_rich",
                True,
                "Пробовать отправку через Rich Messages (нужен бот в чате)",
                validator=loader.validators.Boolean(),
            )
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

    async def _show(self, message, rich_html, fb_title, fb_lines):
        """Rich если можно, иначе запасной вариант. Команду-сообщение при успехе удаляем."""
        if self.config["use_rich"]:
            if await self._send_rich(utils.get_chat_id(message), rich_html):
                try:
                    await message.delete()
                except Exception:
                    pass
                return
        await utils.answer(message, fallback_list(fb_title, fb_lines) + self.strings["fb_note"])

    async def richcmd(self, message):
        """Тест: таблица и сворачиваемые списки"""
        rich = table(
            [["Имя", "<b>RichKit</b>"], ["Автор", "@yfeeeee"]],
        ) + details("Закрытый список", "Скрытый текст") + details("Открытый список", "Видно сразу", True)
        await self._show(
            message, rich, "RichKit", ["Имя: RichKit", "Автор: @yfeeeee", "Rich не сработал, это запасной вариант"]
        )

    async def rmodscmd(self, message):
        """Список модулей: каждый модуль свёрнут, внутри его команды"""
        prefix = self.get_prefix()
        parts = []
        fb = []
        for mod in self.allmodules.modules:
            name = esc(getattr(mod, "strings", {}).get("name", mod.__class__.__name__))
            cmds = module_commands(mod, prefix)
            if cmds:
                rows = [["<code>%s</code>" % esc(c), esc(d)] for c, d in cmds]
                parts.append(details("📦 " + name, table(rows)))
            else:
                parts.append("<b>📦 %s</b>" % name)
            fb.append("%s: %s" % (name, ", ".join(esc(c) for c, _ in cmds) or "без команд"))
        await self._show(message, "".join(parts), "Модули", fb)

    async def rhelpcmd(self, message):
        """<модуль> - команды модуля таблицей в свёрнутом блоке"""
        args = utils.get_args_raw(message).strip().lower()
        if not args:
            await utils.answer(message, "<b>Укажи модуль:</b> <code>%srhelp имя</code>" % self.get_prefix())
            return
        prefix = self.get_prefix()
        for mod in self.allmodules.modules:
            name = getattr(mod, "strings", {}).get("name", mod.__class__.__name__)
            if name.lower() == args:
                cmds = module_commands(mod, prefix)
                rows = [["<code>%s</code>" % esc(c), esc(d)] for c, d in cmds]
                rich = details("📦 " + esc(name), table(rows, ["Команда", "Описание"]), True)
                await self._show(message, rich, esc(name), ["%s %s" % (esc(c), esc(d)) for c, d in cmds])
                return
        await utils.answer(message, self.strings["no_mod"])
