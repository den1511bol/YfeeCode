// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

data class Suggestion(val label: String, val insert: String, val cursorBack: Int = 0, val snippet: Boolean = false)

/** Word completion + snippets. In snippets '|' marks where the cursor ends up; '\n' keeps current indent. */
object Completer {
    /** User snippets (name -> text), saved in SharedPreferences "user_snippets". Developer: @yfeeeee */
    val user = LinkedHashMap<String, String>()
    fun loadUser(ctx: android.content.Context) {
        user.clear()
        ctx.getSharedPreferences("user_snippets", android.content.Context.MODE_PRIVATE).getStringSet("all", emptySet())
            ?.forEach { e -> val k = e.substringBefore('\u0001'); if (k.isNotEmpty() && '\u0001' in e) user[k] = e.substringAfter('\u0001') }
    }
    fun saveUser(ctx: android.content.Context) {
        ctx.getSharedPreferences("user_snippets", android.content.Context.MODE_PRIVATE).edit()
            .putStringSet("all", user.map { it.key + "\u0001" + it.value }.toSet()).apply()
    }

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
            "quote" to "text = \"<blockquote>|Текст цитаты</blockquote>\"\nawait utils.answer(message, text)\n",
            "bq" to "\"<blockquote>|Текст цитаты</blockquote>\"",
            "bqx" to "\"<blockquote expandable>|Длинный текст, свёрнутый по нажатию</blockquote>\"",
            "emoji" to "'<tg-emoji emoji-id=\"|ID\">😈</tg-emoji>'",
            "capq" to "caption = (\n    \"<blockquote>\"\n    \"|Первая строка\\n\"\n    \"Вторая строка\"\n    \"</blockquote>\"\n)\n",
            "bold" to "\"<b>|жирный</b>\"",
            "ital" to "\"<i>|курсив</i>\"",
            "under" to "\"<u>|подчёркнутый</u>\"",
            "strike" to "\"<s>|зачёркнутый</s>\"",
            "spoiler" to "\"<tg-spoiler>|скрытый текст</tg-spoiler>\"",
            "link" to "'<a href=\"|https://\">текст ссылки</a>'",
            "mention" to "'<a href=\"tg://user?id=|123\">имя</a>'",
            "code" to "\"<code>|моноширинный</code>\"",
            "pre" to "'<pre><code class=\"language-python\">|код</code></pre>'",
            "esc" to "import html\ntext = html.escape(|user_text)\n",
            "sfile" to "await self.client.send_file(\n    message.chat_id,\n    |file,\n    caption=caption,\n    reply_to=message.id,\n)\n",
            "sphoto" to "bot.send_photo(message.chat.id, open(\"|photo.jpg\", \"rb\"), caption=\"Подпись\", parse_mode=\"HTML\")\n",
            "saudio" to "bot.send_audio(message.chat.id, open(\"|track.mp3\", \"rb\"), caption=\"Подпись\", parse_mode=\"HTML\")\n",
            "edit" to "await utils.answer(message, \"|новый текст\")\n",
            "reply" to "bot.reply_to(message, \"|Ответ\")\n",
            "del" to "await message.delete()\n",
            "sleep" to "await asyncio.sleep(|1)\n",
            "json" to "import json\n\nwith open(\"|data.json\", \"r\", encoding=\"utf-8\") as f:\n    data = json.load(f)\n",
            "jsave" to "with open(\"|data.json\", \"w\", encoding=\"utf-8\") as f:\n    json.dump(data, f, ensure_ascii=False, indent=2)\n",
            "env" to "import os\nTOKEN = os.environ.get(\"|BOT_TOKEN\")\n",
            "log" to "import logging\nlogging.basicConfig(level=logging.INFO)\nlog = logging.getLogger(__name__)\nlog.info(\"|старт\")\n",
            "fstr" to "f\"|{name}\"",
            "multi" to "\"\"\"\n|Многострочный текст\n\"\"\"",
            "tryx" to "try:\n    |\nexcept Exception as e:\n    await utils.answer(message, f\"Ошибка: {e}\")\n",
            "dl" to "path = await message.download_media(\"|\")\n",
            "reqget" to "import requests\n\nr = requests.get(\"|https://\", timeout=10)\nr.raise_for_status()\ndata = r.json()\n",
            "sql" to "import sqlite3\n\ncon = sqlite3.connect(\"|data.db\")\ncur = con.cursor()\ncur.execute(\"CREATE TABLE IF NOT EXISTS t (id INTEGER PRIMARY KEY, name TEXT)\")\ncon.commit()\n",
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
            "af" to "const | = () => {\n  \n};",
            "lsget" to "const v = JSON.parse(localStorage.getItem(\"|key\") || \"null\");",
            "lsset" to "localStorage.setItem(\"|key\", JSON.stringify(value));",
            "click" to "document.querySelector(\"|#id\").addEventListener(\"click\", () => {\n  \n});",
            "qs" to "const el = document.querySelector(\"|.class\");",
            "qsa" to "document.querySelectorAll(\"|.class\").forEach((el) => {\n  \n});",
            "tmo" to "setTimeout(() => {\n  |\n}, 1000);",
            "int" to "setInterval(() => {\n  |\n}, 1000);",
            "map" to "const result = |arr.map((x) => x * 2);",
            "filter" to "const result = |arr.filter((x) => x > 0);",
            "fe" to "|arr.forEach((x) => {\n  \n});",
            "tpl" to "`|Привет, \${name}`",
            "async" to "async function |name() {\n  \n}",
            "post" to "const res = await fetch(\"|url\", {\n  method: \"POST\",\n  headers: { \"Content-Type\": \"application/json\" },\n  body: JSON.stringify({}),\n});"
        ),
        Lang.HTML to mapOf(
            "html" to "<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\">\n    <title>|</title>\n</head>\n<body>\n    \n</body>\n</html>\n",
            "ul" to "<ul>\n    <li>|</li>\n</ul>",
            "a" to "<a href=\"|\"></a>",
            "img" to "<img src=\"|\">",
            "form" to "<form>\n    <input type=\"text\" name=\"|name\" placeholder=\"Имя\">\n    <button type=\"submit\">Отправить</button>\n</form>",
            "btn" to "<button type=\"button\" id=\"|btn\">Нажми</button>",
            "input" to "<input type=\"text\" id=\"|id\" placeholder=\"Введите текст\">",
            "table" to "<table>\n    <tr><th>|Заголовок</th><th>Колонка</th></tr>\n    <tr><td></td><td></td></tr>\n</table>",
            "nav" to "<nav>\n    <a href=\"#\">|Главная</a>\n    <a href=\"#\">О нас</a>\n</nav>",
            "meta" to "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">",
            "link" to "<link rel=\"stylesheet\" href=\"|style.css\">",
            "script" to "<script src=\"|script.js\"></script>",
            "div" to "<div class=\"|\">\n    \n</div>",
            "sel" to "<select>\n    <option value=\"1\">|Один</option>\n    <option value=\"2\">Два</option>\n</select>"
        ),
        Lang.CSS to mapOf(
            "flex" to "display: flex;\njustify-content: center;\nalign-items: center;|",
            "card" to ".card {\n    padding: 12px;\n    border-radius: 12px;\n    |\n}",
            "grid" to "display: grid;\ngrid-template-columns: repeat(|3, 1fr);\ngap: 12px;",
            "center" to "display: flex;\njustify-content: center;\nalign-items: center;\nmin-height: 100vh;|",
            "media" to "@media (max-width: 600px) {\n    |\n}",
            "shadow" to "box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);|",
            "anim" to "@keyframes |name {\n    from { opacity: 0; }\n    to { opacity: 1; }\n}",
            "trans" to "transition: all 0.3s ease;|",
            "var" to ":root {\n    --main: #b91c1c;\n}\n.x { color: var(--main);| }",
            "hover" to ".|btn:hover {\n    opacity: 0.8;\n}",
            "abs" to "position: absolute;\ntop: 0;\nleft: 0;|",
            "reset" to "* {\n    box-sizing: border-box;\n    margin: 0;\n    padding: 0;\n}|"
        ),
        Lang.CPP to mapOf(
            "main" to "#include <iostream>\n\nint main() {\n    |\n    return 0;\n}",
            "for" to "for (int i = 0; i < |; i++) {\n    \n}",
            "cout" to "std::cout << | << std::endl;",
            "class" to "class | {\npublic:\n    \n};",
            "vec" to "std::vector<int> | = {};",
            "cin" to "std::cin >> |x;",
            "while" to "while (|) {\n    \n}",
            "if" to "if (|) {\n    \n} else {\n    \n}",
            "fn" to "int |name(int a, int b) {\n    return a + b;\n}",
            "str" to "std::string |s = \"\";",
            "map" to "std::map<std::string, int> |m;",
            "rfor" to "for (auto& x : |v) {\n    \n}",
            "sort" to "std::sort(|v.begin(), v.end());",
            "struct" to "struct | {\n    int x;\n    int y;\n};",
            "ptr" to "int* |p = nullptr;"
        ),
        Lang.CSHARP to mapOf(
            "main" to "using System;\n\nclass Program\n{\n    static void Main()\n    {\n        |\n    }\n}",
            "for" to "for (int i = 0; i < |; i++)\n{\n    \n}",
            "cw" to "Console.WriteLine(|);",
            "class" to "class |\n{\n    public string Name { get; set; }\n}",
            "foreach" to "foreach (var x in |)\n{\n    \n}",
            "cr" to "var |x = Console.ReadLine();",
            "if" to "if (|)\n{\n    \n}\nelse\n{\n    \n}",
            "while" to "while (|)\n{\n    \n}",
            "fn" to "static int |Name(int a, int b)\n{\n    return a + b;\n}",
            "list" to "var |items = new List<string>();",
            "dict" to "var |d = new Dictionary<string, int>();",
            "try" to "try\n{\n    |\n}\ncatch (Exception e)\n{\n    Console.WriteLine(e.Message);\n}",
            "switch" to "switch (|x)\n{\n    case 1:\n        break;\n    default:\n        break;\n}",
            "linq" to "var r = |items.Where(x => x > 0).ToList();",
            "prop" to "public string |Name { get; set; }"
        )
    )

    /** Members offered after "name." in Python (Telegram bots, Hikka). Developer: @yfeeeee */
    private val members: Map<String, List<String>> = mapOf(
        "bot" to listOf("send_message", "reply_to", "send_photo", "send_document", "edit_message_text", "delete_message", "answer_callback_query",
            "infinity_polling", "polling", "message_handler", "callback_query_handler", "get_me", "forward_message", "send_chat_action"),
        "message" to listOf("text", "chat", "from_user", "message_id", "reply_to_message", "answer", "reply", "photo", "document", "caption", "date", "delete", "edit_text", "get_args"),
        "call" to listOf("data", "message", "from_user", "answer", "id", "edit_text", "delete"),
        "utils" to listOf("answer", "get_args_raw", "get_args", "get_args_split_by", "escape_html", "get_chat_id", "get_user", "run_sync", "answer_file", "invite_inline_bot"),
        "loader" to listOf("Module", "command", "tds", "watcher", "inline_handler", "callback_handler", "owner", "unrestricted", "ratelimit"),
        "self" to listOf("client", "db", "get", "set", "strings", "config", "inline", "_db", "_client", "allmodules", "tg_id"),
        "client" to listOf("send_message", "send_file", "edit_message", "delete_messages", "get_messages", "get_entity", "get_me", "iter_messages", "download_media", "forward_messages", "send_read_acknowledge"),
        "asyncio" to listOf("sleep", "run", "gather", "create_task", "wait_for", "Queue", "Lock", "get_event_loop", "Event", "TimeoutError"),
        "os" to listOf("path", "listdir", "getenv", "environ", "makedirs", "remove", "getcwd", "rename"),
        "json" to listOf("dumps", "loads", "dump", "load"),
        "time" to listOf("sleep", "time", "strftime", "perf_counter"),
        "random" to listOf("choice", "randint", "shuffle", "random", "sample")
    )

    fun suggest(text: String, cursor: Int, lang: Lang): List<Suggestion> {
        var i = cursor
        while (i > 0 && (text[i - 1].isLetterOrDigit() || text[i - 1] == '_')) i--
        val prefix = text.substring(i, cursor)
        if (i > 0 && text[i - 1] == '<' && (lang == Lang.PYTHON || lang == Lang.JS)) {
            val tags = listOf(
                "b" to "b>|</b>", "i" to "i>|</i>", "u" to "u>|</u>", "s" to "s>|</s>",
                "code" to "code>|</code>", "pre" to "pre>|</pre>",
                "blockquote" to "blockquote>|</blockquote>",
                "a" to "a href=\"|\"></a>",
                "tg-spoiler" to "tg-spoiler>|</tg-spoiler>",
                "tg-emoji" to "tg-emoji emoji-id=\"|\">🙂</tg-emoji>"
            )
            val hit = tags.filter { it.first.startsWith(prefix) }.map { (n, raw) ->
                val pos = raw.indexOf('|'); val clean = raw.replace("|", "")
                Suggestion("<" + n + ">", clean, clean.length - pos, snippet = true)
            }
            if (hit.isNotEmpty()) return hit
        }
        if (lang == Lang.PYTHON && i > 1 && text[i - 1] == '.') {
            var j = i - 1
            while (j > 0 && (text[j - 1].isLetterOrDigit() || text[j - 1] == '_')) j--
            val obj = text.substring(j, i - 1)
            val hit = members[obj]?.filter { it.startsWith(prefix, ignoreCase = true) && it != prefix }
            if (!hit.isNullOrEmpty()) return hit.take(7).map { Suggestion(it, it) }
        }
        if (prefix.isEmpty()) return emptyList()
        val out = mutableListOf<Suggestion>()
        (user[prefix] ?: snippets[lang]?.get(prefix))?.let { raw ->
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
