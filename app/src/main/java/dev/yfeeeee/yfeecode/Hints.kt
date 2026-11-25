// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Offline rule-based hints. Returns short messages about the current code. */
object Hints {

    fun analyze(code: String, lang: Lang, cursorLine: Int): List<String> {
        val out = mutableListOf<String>()
        val lines = code.split("\n")
        val cur = lines.getOrNull(cursorLine)?.trim().orEmpty()

        // brackets balance (ignoring strings, simple)
        val stack = ArrayDeque<Char>()
        var quote: Char? = null
        for (ch in code) {
            if (quote != null) { if (ch == quote || ch == '\n') quote = null; continue }
            if (ch == '"' || ch == '\'') { quote = ch; continue }
            when (ch) {
                '(', '[', '{' -> stack.addLast(ch)
                ')', ']', '}' -> {
                    val open = stack.removeLastOrNull()
                    if (open == null || AutoFix.pairs[open] != ch)
                        out += "Лишняя или не парная скобка «$ch»"
                }
            }
        }
        if (stack.isNotEmpty()) out += "Не закрыта скобка «${stack.last()}»"

        when (lang) {
            Lang.PYTHON -> { pythonHints(cur, lines, cursorLine, out); pythonFile(code, out) }
            Lang.JS -> jsHints(cur, out)
            Lang.HTML -> htmlHints(code, out)
            Lang.CSS -> if (cur.isNotEmpty() && !cur.endsWith(";") && !cur.endsWith("{") &&
                !cur.endsWith("}") && cur.contains(":"))
                out += "CSS: в конце свойства нужна «;»"
            Lang.CPP -> cppHints(cur, code, out)
            Lang.CSHARP -> csHints(cur, out)
            else -> {}
        }
        return out.distinct().take(3)
    }

    private fun pythonHints(cur: String, lines: List<String>, idx: Int, out: MutableList<String>) {
        val blocks = listOf("def ", "class ", "if ", "elif ", "for ", "while ", "try", "except", "with ", "else", "finally")
        if (blocks.any { cur.startsWith(it) } && !cur.endsWith(":") && !cur.contains("#"))
            out += "Python: строка блока должна заканчиваться «:»"
        if (cur.startsWith("def ") && cur.endsWith(":")) {
            val next = lines.drop(idx + 1).firstOrNull { it.isNotBlank() }
            if (next == null || next.length - next.trimStart().length <= lines[idx].length - lines[idx].trimStart().length)
                out += "Python: после def нужно тело функции с отступом"
        }
        if (cur.startsWith("except") && lines.take(idx).none { it.trim().startsWith("try") })
            out += "Python: except без try выше"
        if (cur.startsWith("print ") && !cur.startsWith("print("))
            out += "Python 3: print нужно вызывать со скобками"
        if (cur.startsWith("async def") || cur.contains("await "))
            if (lines.none { it.contains("import asyncio") || it.contains("aiogram") || it.contains("pyrogram") })
                out += "Для async-кода обычно нужен import asyncio"
        if (cur.contains("= =") || Regex("\\bif .*[^=!<>]=[^=].*:").containsMatchIn(cur))
            out += "В условии, скорее всего, нужен «==» вместо «=»"
        if (cur.startsWith("@loader.command") || cur.startsWith("class ") && cur.contains("loader.Module"))
            out += "Heroku-модуль: нужен @loader.tds и strings = {\"name\": ...}"
    }

    /** Whole-file hints, mostly for Telegram bots. */
    private fun pythonFile(code: String, out: MutableList<String>) {
        if (Regex("""\d{8,}:[A-Za-z0-9_-]{30,}""").containsMatchIn(code))
            out += "Токен бота в коде! Храни его в token.txt и не выкладывай на GitHub"
        if (code.contains("TeleBot(") && !code.contains("polling"))
            out += "Бот создан, но не запущен: добавь bot.infinity_polling()"
        if (code.contains("aiogram") && code.contains("Dispatcher") &&
            !code.contains("start_polling") && !code.contains("run_polling"))
            out += "aiogram: не хватает запуска executor.start_polling(dp)"
        if (code.contains("time.sleep") && code.contains("async def"))
            out += "В async-коде вместо time.sleep используй await asyncio.sleep"
        if (code.contains("@loader.command") && !code.contains("loader.tds"))
            out += "Модуль Heroku: над классом нужен @loader.tds"
        if (Regex("""\bawait\b""").containsMatchIn(code) && !code.contains("async def") && !code.contains("async with"))
            out += "await используется вне async def"
        if (Regex("""open\([^)]*\)""").containsMatchIn(code) && !code.contains("with open") && code.contains("write"))
            out += "Файл лучше открывать через with open(...) as f:"
    }

    private fun jsHints(cur: String, out: MutableList<String>) {
        if (cur.startsWith("function") && !cur.contains("{") && !cur.endsWith("{"))
            out += "JS: после function(...) нужен блок { }"
        if ((cur.startsWith("const ") || cur.startsWith("let ") || cur.startsWith("var ")) &&
            !cur.endsWith(";") && !cur.endsWith(",") && !cur.endsWith("{") && !cur.endsWith("(") && cur.contains("="))
            out += "JS: в конце строки обычно ставят «;»"
        if (cur.contains("==") && !cur.contains("==="))
            out += "JS: лучше использовать «===» вместо «==»"
        if (cur.contains("await ") )
            out += "JS: await работает только внутри async-функции"
    }

    private fun htmlHints(code: String, out: MutableList<String>) {
        val low = code.lowercase()
        if (low.isNotBlank() && !low.contains("<!doctype")) out += "HTML: добавь <!DOCTYPE html> в начало"
        if (low.contains("<html") && !low.contains("</html>")) out += "HTML: не закрыт тег </html>"
        if (low.contains("<body") && !low.contains("</body>")) out += "HTML: не закрыт тег </body>"
        if (low.contains("<head") && !low.contains("<title")) out += "HTML: в <head> не хватает <title>"
        if (low.contains("<script") && !low.contains("</script>")) out += "HTML: не закрыт </script>"
    }

    private fun cppHints(cur: String, code: String, out: MutableList<String>) {
        if (cur.contains("cout") && !code.contains("#include <iostream>"))
            out += "C++: для cout нужен #include <iostream>"
        if (cur.contains("string ") && !code.contains("#include <string>") && !code.contains("<iostream>"))
            out += "C++: для string нужен #include <string>"
        if (cur.startsWith("int main") && !code.contains("return"))
            out += "C++: main обычно завершают return 0;"
        if (cur.isNotEmpty() && !cur.endsWith(";") && !cur.endsWith("{") && !cur.endsWith("}") &&
            !cur.startsWith("#") && !cur.startsWith("//") && !cur.endsWith(")") && !cur.endsWith(",") && cur.contains("="))
            out += "C++: не хватает «;» в конце"
    }

    private fun csHints(cur: String, out: MutableList<String>) {
        if (cur.startsWith("Console.") && !cur.endsWith(";"))
            out += "C#: не хватает «;» в конце"
        if (cur.startsWith("class ") && !cur.contains("{"))
            out += "C#: после class нужен блок { }"
        if (cur.contains("await ") )
            out += "C#: await работает только в async-методе (Task)"
    }
}
