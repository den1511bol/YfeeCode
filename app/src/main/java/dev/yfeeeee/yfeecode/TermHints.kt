// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import java.io.File

/** Clickable word suggestions for the terminal input line. Tapping one appends the word to the line (it is not sent). */
object TermHints {
    private val shCommands = listOf("ls", "ls -la", "pwd", "cd", "cat", "echo", "mkdir", "rm", "cp", "mv", "clear", "help",
        "run", "stop", "mod", "token", "get", "py", "sh")
    private val fileCommands = setOf("run", "mod", "cat", "rm", "cp", "mv", "cd", "ls")

    private val pyWords = listOf("print(", "input(", "len(", "range(", "int(", "str(", "float(", "list(", "dict(", "set(", "tuple(",
        "open(", "sorted(", "sum(", "min(", "max(", "abs(", "type(", "isinstance(", "enumerate(", "zip(", "map(", "filter(", "dir(", "help(",
        "import", "from", "def", "class", "if", "elif", "else", "for", "while", "return", "in", "not", "and", "or", "is", "lambda",
        "try", "except", "finally", "with", "as", "pass", "break", "continue", "True", "False", "None", "async", "await")
    private val pyModules = listOf("os", "sys", "json", "re", "math", "random", "time", "datetime", "asyncio", "requests",
        "telebot", "aiogram", "pyrogram", "pathlib", "collections", "itertools")
    private val pyStarters = listOf("print(", "import", "for", "def", "if", "class", "x =")

    /** The word being typed: the text after the last separator. */
    fun currentWord(input: String): String = input.takeLastWhile { it.isLetterOrDigit() || it == '_' || it == '.' || it == '-' }

    fun suggest(input: String, mode: TermMode, cwd: File, history: List<String>): List<String> {
        val word = currentWord(input)
        val before = input.dropLast(word.length)
        val out = LinkedHashSet<String>()
        fun add(list: Collection<String>) {
            list.filter { it.startsWith(word, ignoreCase = true) && !it.equals(word, ignoreCase = true) }.forEach { out += it }
        }
        if (mode == TermMode.SH) {
            val first = before.trim().substringBefore(' ').takeIf { before.isNotBlank() }
            if (first == null) add(shCommands)
            else if (first in fileCommands) {
                val names = (cwd.list() ?: emptyArray()).sorted()
                add(names.filter { n -> first != "run" && first != "mod" || n.endsWith(".py") || n.endsWith(".js") || File(cwd, n).isDirectory })
                if (first == "cd") add(listOf("..", "~"))
            }
        } else {
            val prev = before.trim().substringAfterLast(' ')
            if (prev == "import" || (before.trimStart().startsWith("from ") && prev == "from")) add(pyModules)
            else {
                if (word.isNotEmpty()) add(pyWords)
                if (word.length >= 2) {
                    val seen = history.flatMap { Regex("[A-Za-z_][A-Za-z0-9_]{2,}").findAll(it).map { m -> m.value }.toList() }.distinct()
                    add(seen)
                }
                if (word.isEmpty() && before.isBlank()) add(pyStarters)
            }
        }
        return out.take(10)
    }

    /** What the line becomes after tapping [s]: the current word is replaced by [s] and a space follows (except after "("). */
    fun apply(input: String, s: String): String {
        val word = currentWord(input)
        val base = input.dropLast(word.length)
        return base + s + if (s.endsWith("(") || s.endsWith(" ")) "" else " "
    }
}
