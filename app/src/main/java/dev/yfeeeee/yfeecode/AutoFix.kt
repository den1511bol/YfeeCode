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
        val raw = code.replace("\r\n", "\n").split("\n").map { it.replace("\t", "    ") }
        // For Python: lines that start a real statement (not inside brackets or a triple-quoted string).
        val stmt = BooleanArray(raw.size)
        if (lang == Lang.PYTHON) {
            var depth = 0
            var tri: String? = null
            raw.forEachIndexed { i, l ->
                stmt[i] = depth == 0 && tri == null
                var j = 0
                while (j < l.length) {
                    val t = tri
                    if (t != null) {
                        if (l.startsWith(t, j)) { tri = null; j += 3 } else j++
                        continue
                    }
                    val c = l[j]
                    if (c == '#') break
                    if (l.startsWith("\"\"\"", j) || l.startsWith("'''", j)) { tri = l.substring(j, j + 3); j += 3; continue }
                    if (c == '"' || c == '\'') {
                        var k = j + 1
                        while (k < l.length && l[k] != c) { if (l[k] == '\\') k++; k++ }
                        j = k + 1; continue
                    }
                    if (c == '(' || c == '[' || c == '{') depth++
                    if ((c == ')' || c == ']' || c == '}') && depth > 0) depth--
                    j++
                }
            }
        }
        fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
        var unit = 0
        if (lang == Lang.PYTHON) {
            raw.forEachIndexed { i, l ->
                if (stmt[i] && l.isNotBlank()) {
                    val n = l.length - l.trimStart(' ').length
                    if (n > 0) unit = gcd(unit, n)
                }
            }
        }
        val lines = raw.mapIndexed { i, l ->
            var x = l.trimEnd()
            if (lang == Lang.PYTHON && stmt[i] && x.isNotBlank()) {
                val n = x.length - x.trimStart(' ').length
                if (n > 0 && unit != 4) {
                    val fixed = if (unit in 2..8) n / unit * 4 else ((n + 2) / 4) * 4
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
