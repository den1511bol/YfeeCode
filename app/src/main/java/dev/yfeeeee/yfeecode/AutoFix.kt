// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Cheap, safe fixes for typos typical for phone typing. */
object AutoFix {

    /** Collapses repeated spaces outside of string literals, keeps leading indent. */
    fun cleanLine(line: String, keepIndent: Boolean = true): String {
        val indentLen = line.length - line.trimStart(' ', '\t').length
        val indent = if (keepIndent) line.substring(0, indentLen) else ""
        val body = line.substring(indentLen).trimEnd()
        val sb = StringBuilder()
        var quote: Char? = null
        var prevSpace = false
        for (c in body) {
            if (quote != null) {
                sb.append(c)
                if (c == quote) quote = null
                continue
            }
            if (c == '"' || c == '\'' || c == '`') {
                quote = c; sb.append(c); prevSpace = false; continue
            }
            if (c == ' ') {
                if (!prevSpace) sb.append(c)
                prevSpace = true
            } else {
                sb.append(c); prevSpace = false
            }
        }
        return indent + sb.toString()
    }

    /** Terminal command: trim and collapse spaces outside quotes. */
    fun command(cmd: String): String = cleanLine(cmd.trim(), keepIndent = false)

    /** Whole file fix: tabs, trailing spaces, odd Python indentation, final newline. */
    fun fixAll(code: String, lang: Lang): String {
        val lines = code.replace("\r\n", "\n").split("\n").map { l ->
            var x = l.replace("\t", "    ").trimEnd()
            if (lang == Lang.PYTHON) {
                val n = x.length - x.trimStart(' ').length
                if (n % 4 != 0 && n > 0) {
                    val fixed = ((n + 2) / 4) * 4
                    x = " ".repeat(fixed) + x.trimStart(' ')
                }
            }
            x
        }
        var res = lines.joinToString("\n").trimEnd('\n')
        res += "\n"
        return res
    }

    /** Indent for the new line after Enter. */
    fun nextIndent(prevLine: String, lang: Lang): String {
        val base = prevLine.substring(0, prevLine.length - prevLine.trimStart(' ').length)
        val t = prevLine.trimEnd()
        val opens = when (lang) {
            Lang.PYTHON -> t.endsWith(":")
            Lang.HTML -> false
            else -> t.endsWith("{") || t.endsWith("(") || t.endsWith("[")
        }
        return if (opens) "$base    " else base
    }

    val pairs = mapOf('(' to ')', '[' to ']', '{' to '}', '"' to '"', '\'' to '\'')
}
