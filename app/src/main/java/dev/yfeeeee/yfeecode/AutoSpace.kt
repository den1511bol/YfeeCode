// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/**
 * Automatic spaces around operators while typing: "name=" -> "name = ", "a,b" -> "a, b", "i+=" -> "i += ".
 * Works on the text before the caret. Returns null when nothing special should happen.
 */
object AutoSpace {
    /** [del] characters are removed from the end of the text before the caret, then [ins] is inserted. */
    data class Edit(val del: Int, val ins: String)

    private fun inStringOrComment(line: String, lang: Lang): Boolean {
        var q: Char? = null
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (q != null) {
                if (c == '\\') i++ else if (c == q) q = null
            } else {
                if (c == '"' || c == '\'' || (c == '`' && lang == Lang.JS)) q = c
                else if (lang == Lang.PYTHON && c == '#') return true
                else if (lang != Lang.PYTHON && c == '/' && line.getOrNull(i + 1) == '/') return true
            }
            i++
        }
        return q != null
    }

    private fun parenDepth(line: String): Int = line.count { it == '(' } - line.count { it == ')' }

    private fun operandEnd(ch: Char?) = ch != null && (ch.isLetterOrDigit() || ch == '_' || ch == ')' || ch == ']' || ch == '"' || ch == '\'')

    fun apply(before: String, after: String, s: String, lang: Lang): Edit? {
        if (s.length != 1) return null
        val c = s[0]
        if (c !in ",=+-*/%<>!:") return null
        if (lang == Lang.HTML || lang == Lang.TEXT) return null
        val lineStart = before.lastIndexOf('\n') + 1
        val line = before.substring(lineStart)
        if (inStringOrComment(line, lang)) return null

        if (lang == Lang.CSS) {
            // "color:red" -> "color: red" inside a rule (indented line), not for selectors like a:hover
            return if (c == ':' && line.isNotBlank() && line.first() == ' ' && line.trim().none { it == ' ' || it == '{' } && after.firstOrNull() != ' ')
                Edit(0, ": ") else null
        }

        val next = after.firstOrNull()
        val last = before.lastOrNull()
        val trailOk = next == null || next == ' ' || next == '\n'

        when (c) {
            ',' -> return if (next == ' ') null else Edit(0, ", ")
            ':' -> return null
            '!' -> return null
        }

        // "!" directly before "=" -> " != "
        if (c == '=' && last == '!' && before.length >= 1) {
            val prevCh = before.getOrNull(before.length - 2)
            val lead = if (prevCh == null || prevCh == ' ' || prevCh == '\n') "" else " "
            return if (lead.isEmpty()) Edit(0, "= ") else Edit(1, " != ")
        }

        // combine with an operator we already surrounded with spaces: " = " + "=" -> " == "
        if (c == '=') {
            for (op in "+-*/%<>=!") {
                if (before.endsWith(" $op ")) {
                    return Edit(3, " $op= ")
                }
            }
            if (before.endsWith(" == ") && lang == Lang.JS) return Edit(4, " === ")
            if (before.endsWith(" != ") && lang == Lang.JS) return Edit(4, " !== ")
        }
        if (c == '>') {
            if (before.endsWith(" = ")) return Edit(3, " => ")          // arrow function / lambda-like
            if (before.endsWith(" - ")) return if (lang == Lang.PYTHON) Edit(3, " -> ") else if (lang == Lang.CPP || lang == Lang.CSHARP) Edit(3, "->") else null
        }
        if ((c == '+' || c == '-') && before.endsWith(" $c ")) return Edit(3, "$c$c")   // i++ / i--
        if (c == '*' && before.endsWith(" * ") && (lang == Lang.PYTHON || lang == Lang.JS)) return Edit(3, " ** ")
        if (c == '/' && before.endsWith(" / ") && lang == Lang.PYTHON) return Edit(3, " // ")

        val lead = if (last == null || last == ' ' || last == '\n' || last == '\t') "" else " "
        val trail = if (trailOk) " " else ""

        when (c) {
            '=' -> {
                // keyword arguments: f(a=1) stays tight in Python
                if (lang == Lang.PYTHON && parenDepth(line) > 0) return null
                if (last == '=' || last == '<' || last == '>' ) return null
                return Edit(0, "$lead=$trail")
            }
            '+', '-', '%' -> {
                if (!operandEnd(before.trimEnd().lastOrNull())) return null
                return Edit(0, "$lead$c$trail")
            }
            '*' -> {
                if (lang == Lang.CPP || lang == Lang.CSHARP) return null
                if (!operandEnd(before.trimEnd().lastOrNull())) return null
                return Edit(0, "$lead$c$trail")
            }
            '/' -> {
                if (lang != Lang.PYTHON) return null
                if (!operandEnd(before.trimEnd().lastOrNull())) return null
                return Edit(0, "$lead$c$trail")
            }
            '<', '>' -> {
                if (lang != Lang.PYTHON && lang != Lang.JS) return null
                if (!operandEnd(before.trimEnd().lastOrNull())) return null
                return Edit(0, "$lead$c$trail")
            }
        }
        return null
    }
}
