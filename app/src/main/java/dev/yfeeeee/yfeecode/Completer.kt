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
            "try" to "try:\n    |\nexcept Exception as e:\n    print(e)",
            "main" to "if __name__ == \"__main__\":\n    |",
            "async" to "async def |():\n    pass",
            "tg" to "import telebot\n\nTOKEN = open(\"token.txt\").read().strip()\nbot = telebot.TeleBot(TOKEN)\n\n\n@bot.message_handler(commands=[\"start\"])\ndef start(message):\n    bot.reply_to(message, \"|\")\n\n\nbot.infinity_polling()\n",
            "mod" to "from .. import loader, utils\n\n\n@loader.tds\nclass |Mod(loader.Module):\n    \"\"\"Описание\"\"\"\n\n    strings = {\"name\": \"\"}\n\n    @loader.command()\n    async def hellocmd(self, message):\n        \"\"\"Команда\"\"\"\n        await utils.answer(message, \"Привет\")\n"
        ),
        Lang.JS to mapOf(
            "fn" to "function |() {\n  \n}",
            "for" to "for (let i = 0; i < |; i++) {\n  \n}",
            "if" to "if (|) {\n  \n}",
            "log" to "console.log(|);",
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
            "cout" to "std::cout << | << std::endl;"
        ),
        Lang.CSHARP to mapOf(
            "main" to "using System;\n\nclass Program\n{\n    static void Main()\n    {\n        |\n    }\n}",
            "for" to "for (int i = 0; i < |; i++)\n{\n    \n}",
            "cw" to "Console.WriteLine(|);"
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
            Regex("[A-Za-z_][A-Za-z0-9_]{2,}").findAll(text).forEach { pool.add(it.value) }
            pool.filter { it.startsWith(prefix, ignoreCase = true) && it != prefix }
                .take(6).forEach { out += Suggestion(it, it) }
        }
        return out.take(7)
    }
}
