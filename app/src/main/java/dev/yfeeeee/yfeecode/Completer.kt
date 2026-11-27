// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

data class Suggestion(val label: String, val insert: String, val cursorBack: Int = 0, val snippet: Boolean = false)

/** Word completion + snippets. In snippets '|' marks where the cursor ends up; '\n' keeps current indent. */
object Completer {
    private val words: Map<Lang, List<String>> = mapOf(
        Lang.PYTHON to listOf("def", "class", "import", "from", "return", "print", "range", "len", "self", "async", "await",
            "elif", "else", "while", "for", "try", "except", "finally", "with", "lambda", "None", "True", "False", "isinstance",
            "enumerate", "append", "asyncio", "telebot", "aiogram", "loader", "utils", "message", "strings", "pass", "break", "continue"),
        Lang.JS to listOf("function", "const", "let", "var", "return", "console", "async", "await", "fetch", "await", "document",
            "addEventListener", "querySelector", "JSON", "stringify", "Promise", "setTimeout", "length", "push", "forEach", "map", "filter"),
        Lang.HTML to listOf("div", "span", "section", "header", "footer", "button", "input", "form", "script", "style", "class", "href", "src"),
        Lang.CSS to listOf("display", "flex", "grid", "margin", "padding", "border", "radius", "background", "color", "font", "justify-content", "align-items", "position", "transition"),
        Lang.CPP to listOf("include", "iostream", "vector", "string", "namespace", "return", "const", "static", "struct", "class", "nullptr", "cout", "endl", "cin", "size_t"),
        Lang.CSHARP to listOf("using", "namespace", "class", "static", "void", "string", "Console", "WriteLine", "ReadLine", "List", "async", "await", "Task", "return", "foreach", "public", "private")
    )

    private val snippets: Map<Lang, Map<String, String>> = mapOf(
        Lang.PYTHON to mapOf(
            "def" to "def |():\n    pass",
            "class" to "class |:\n    def __init__(self):\n        pass",
            "for" to "for i in range(|):\n    pass",
            "if" to "if |:\n    pass",
            "while" to "while |:\n    pass",
            "with" to "with open(\"|\") as f:\n    data = f.read()",
            "try" to "try:\n    |\nexcept Exception as e:\n    print(e)",
            "main" to "if __name__ == \"__main__\":\n    |",
            "async" to "async def |():\n    pass",
            "tg" to "import telebot\n\nTOKEN = open(\"token.txt\").read().strip()\nbot = telebot.TeleBot(TOKEN)\n\n\n@bot.message_handler(commands=[\"start\"])\ndef start(message):\n    bot.reply_to(message, \"|\")\n\n\nbot.infinity_polling()\n",
            "mod" to "from .. import loader, utils\n\n\n@loader.tds\nclass |Mod(loader.Module):\n    \"\"\"Описание\"\"\"\n\n    strings = {\"name\": \"\"}\n\n    @loader.command()\n    async def hellocmd(self, message):\n        \"\"\"Команда\"\"\"\n        await utils.answer(message, \"Привет\")\n",
            "hcmd" to "@loader.command()\nasync def |cmd(self, message):\n    \"\"\"Описание команды\"\"\"\n    args = utils.get_args_raw(message)\n    await utils.answer(message, args)\n",
            "hwatch" to "@loader.watcher(only_messages=True)\nasync def watcher(self, message):\n    if |:\n        return\n",
            "hcfg" to "self.config = loader.ModuleConfig(\n    loader.ConfigValue(\"|\", \"\", \"Описание\"),\n)\n",
            "aio" to "import asyncio\nfrom aiogram import Bot, Dispatcher, types\nfrom aiogram.filters import Command\n\nbot = Bot(token=open(\"token.txt\").read().strip())\ndp = Dispatcher()\n\n\n@dp.message(Command(\"start\"))\nasync def start(message: types.Message):\n    await message.answer(\"|\")\n\n\nasync def main():\n    await dp.start_polling(bot)\n\n\nasyncio.run(main())\n",
            "aiomsg" to "@dp.message()\nasync def |handler(message: types.Message):\n    await message.answer(message.text)\n",
            "aiofsm" to "from aiogram.fsm.context import FSMContext\nfrom aiogram.fsm.state import State, StatesGroup\n\n\nclass Form(StatesGroup):\n    name = State()\n\n\n@dp.message(Command(\"form\"))\nasync def form_start(message: types.Message, state: FSMContext):\n    await state.set_state(Form.name)\n    await message.answer(\"|Как тебя зовут?\")\n\n\n@dp.message(Form.name)\nasync def form_name(message: types.Message, state: FSMContext):\n    await state.clear()\n    await message.answer(\"Привет, \" + (message.text or \"\"))\n",
            "aiokb" to "from aiogram.types import InlineKeyboardButton, InlineKeyboardMarkup\n\nkb = InlineKeyboardMarkup(inline_keyboard=[\n    [InlineKeyboardButton(text=\"|Кнопка\", callback_data=\"btn1\")],\n])\n\n\n@dp.callback_query(lambda c: c.data == \"btn1\")\nasync def on_btn1(call: types.CallbackQuery):\n    await call.answer(\"Нажато\")\n",
            "tgkb" to "from telebot import types\n\nkb = types.InlineKeyboardMarkup()\nkb.add(types.InlineKeyboardButton(\"|Кнопка\", callback_data=\"btn1\"))\nbot.send_message(message.chat.id, \"Выбери:\", reply_markup=kb)\n",
            "hinl" to "@loader.command()\nasync def |inlcmd(self, message):\n    \"\"\"Сообщение с кнопкой\"\"\"\n    await self.inline.form(\n        text=\"Привет\",\n        message=message,\n        reply_markup=[[{\"text\": \"Кнопка\", \"callback\": self.on_btn}]],\n    )\n\nasync def on_btn(self, call):\n    await call.answer(\"Нажато\")\n",
            "tgcb" to "@bot.callback_query_handler(func=lambda c: True)\ndef |callback(call):\n    bot.answer_callback_query(call.id)\n"
        ),
        Lang.JS to mapOf(
            "fn" to "function |() {\n  \n}",
            "for" to "for (let i = 0; i < |; i++) {\n  \n}",
            "if" to "if (|) {\n  \n}",
            "log" to "console.log(|);",
            "try" to "try {\n  |\n} catch (e) {\n  console.error(e);\n}",
            "fetch" to "const res = await fetch(\"|\");\nconst data = await res.json();",
            "class" to "class | {\n  constructor() {\n  }\n}",
            "af" to "const | = () => {\n  \n};"
        ),
        Lang.HTML to mapOf(
            "html" to "<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\">\n    <title>|</title>\n</head>\n<body>\n    \n</body>\n</html>\n",
            "ul" to "<ul>\n    <li>|</li>\n</ul>",
            "a" to "<a href=\"|\"></a>",
            "img" to "<img src=\"|\">"
        ),
        Lang.CSS to mapOf(
            "flex" to "display: flex;\njustify-content: center;\nalign-items: center;|",
            "card" to ".card {\n    padding: 12px;\n    border-radius: 12px;\n    |\n}"
        ),
        Lang.CPP to mapOf(
            "main" to "#include <iostream>\n\nint main() {\n    |\n    return 0;\n}",
            "for" to "for (int i = 0; i < |; i++) {\n    \n}",
            "cout" to "std::cout << | << std::endl;",
            "class" to "class | {\npublic:\n    \n};",
            "vec" to "std::vector<int> | = {};"
        ),
        Lang.CSHARP to mapOf(
            "main" to "using System;\n\nclass Program\n{\n    static void Main()\n    {\n        |\n    }\n}",
            "for" to "for (int i = 0; i < |; i++)\n{\n    \n}",
            "cw" to "Console.WriteLine(|);",
            "class" to "class |\n{\n    public string Name { get; set; }\n}",
            "foreach" to "foreach (var x in |)\n{\n    \n}"
        )
    )

    fun suggest(text: String, cursor: Int, lang: Lang): List<Suggestion> {
        var i = cursor
        while (i > 0 && (text[i - 1].isLetterOrDigit() || text[i - 1] == '_')) i--
        val prefix = text.substring(i, cursor)
        if (prefix.isEmpty()) return emptyList()
        val out = mutableListOf<Suggestion>()
        snippets[lang]?.get(prefix)?.let { raw ->
            val lineStart = text.lastIndexOf('\n', i - 1) + 1
            val indent = text.substring(lineStart, i).takeWhile { it == ' ' }
            val body = raw.replace("\n", "\n$indent")
            val pos = body.indexOf('|')
            val clean = body.replace("|", "")
            out += Suggestion("⚡ $prefix", clean, if (pos >= 0) clean.length - pos else 0, snippet = true)
        }
        if (prefix.length >= 2) {
            val pool = LinkedHashSet<String>()
            pool.addAll(words[lang].orEmpty())
            if (text.length < 200_000) Regex("[A-Za-z_][A-Za-z0-9_]{2,}").findAll(text).forEach { pool.add(it.value) }
            pool.filter { it.startsWith(prefix, ignoreCase = true) && it != prefix }
                .take(6).forEach { out += Suggestion(it, it) }
        }
        return out.take(7)
    }
}
