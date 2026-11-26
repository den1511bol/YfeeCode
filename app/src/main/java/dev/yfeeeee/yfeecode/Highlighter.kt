// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

object Highlighter {
    private val kwKeyword = Color(0xFFF48972)
    private val kwString = Color(0xFFA5D6A7)
    private val kwComment = Color(0xFFA87F81)
    private val kwNumber = Color(0xFF79C0FF)
    private val kwBase = Color(0xFFE6EDF3)

    private val keywords: Map<Lang, Set<String>> = mapOf(
        Lang.PYTHON to setOf("def", "class", "if", "elif", "else", "for", "while", "return", "import",
            "from", "as", "try", "except", "finally", "with", "async", "await", "lambda", "pass",
            "break", "continue", "None", "True", "False", "in", "is", "not", "and", "or", "raise", "yield"),
        Lang.JS to setOf("function", "const", "let", "var", "if", "else", "for", "while", "return",
            "class", "new", "async", "await", "try", "catch", "finally", "import", "export", "from",
            "null", "undefined", "true", "false", "this", "switch", "case", "break", "continue"),
        Lang.CPP to setOf("int", "float", "double", "char", "void", "bool", "class", "struct", "if",
            "else", "for", "while", "return", "include", "using", "namespace", "std", "new", "delete",
            "const", "static", "public", "private", "true", "false", "nullptr", "auto"),
        Lang.CSHARP to setOf("using", "namespace", "class", "public", "private", "static", "void",
            "int", "string", "bool", "var", "new", "if", "else", "for", "foreach", "while", "return",
            "async", "await", "try", "catch", "finally", "null", "true", "false", "this"),
        Lang.CSS to setOf(),
        Lang.HTML to setOf(),
        Lang.TEXT to setOf()
    )

    fun highlight(code: String, lang: Lang): AnnotatedString =
        if (code.length > 120_000) AnnotatedString(code) else highlightFull(code, lang)

    private fun highlightFull(code: String, lang: Lang): AnnotatedString = buildAnnotatedString {
        val kws = keywords[lang] ?: emptySet()
        val lineComment = when (lang) {
            Lang.PYTHON -> "#"
            Lang.JS, Lang.CPP, Lang.CSHARP -> "//"
            else -> null
        }
        var i = 0
        val n = code.length
        while (i < n) {
            val c = code[i]
            when {
                lineComment != null && code.startsWith(lineComment, i) -> {
                    val end = code.indexOf('\n', i).let { if (it < 0) n else it }
                    withStyle(SpanStyle(color = kwComment)) { append(code.substring(i, end)) }
                    i = end
                }
                lang != Lang.PYTHON && lang != Lang.HTML && lang != Lang.TEXT && code.startsWith("/*", i) -> {
                    val e = code.indexOf("*/", i + 2).let { if (it < 0) n else it + 2 }
                    withStyle(SpanStyle(color = kwComment)) { append(code.substring(i, e)) }
                    i = e
                }
                lang == Lang.PYTHON && (code.startsWith("\"\"\"", i) || code.startsWith("'''", i)) -> {
                    // triple-quoted string / docstring, may span many lines
                    val q = code.substring(i, i + 3)
                    val e = code.indexOf(q, i + 3).let { if (it < 0) n else it + 3 }
                    withStyle(SpanStyle(color = kwString)) { append(code.substring(i, e)) }
                    i = e
                }
                lang == Lang.HTML && code.startsWith("<!--", i) -> {
                    val e = code.indexOf("-->", i).let { if (it < 0) n else it + 3 }
                    withStyle(SpanStyle(color = kwComment)) { append(code.substring(i, e)) }
                    i = e
                }
                c == '"' || c == '\'' || (c == '`' && lang == Lang.JS) -> {
                    var j = i + 1
                    while (j < n && code[j] != c && code[j] != '\n') {
                        if (code[j] == '\\' && j + 1 < n) j++
                        j++
                    }
                    j = minOf(j + 1, n)
                    withStyle(SpanStyle(color = kwString)) { append(code.substring(i, j)) }
                    i = j
                }
                c.isDigit() -> {
                    var j = i
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '.')) j++
                    withStyle(SpanStyle(color = kwNumber)) { append(code.substring(i, j)) }
                    i = j
                }
                c.isLetter() || c == '_' -> {
                    var j = i
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_')) j++
                    val w = code.substring(i, j)
                    val color = if (w in kws) kwKeyword else kwBase
                    withStyle(SpanStyle(color = color)) { append(w) }
                    i = j
                }
                lang == Lang.HTML && c == '<' -> {
                    val e = code.indexOf('>', i).let { if (it < 0) n else it + 1 }
                    withStyle(SpanStyle(color = kwKeyword)) { append(code.substring(i, e)) }
                    i = e
                }
                else -> {
                    withStyle(SpanStyle(color = kwBase)) { append(c.toString()) }
                    i++
                }
            }
        }
    }
}
