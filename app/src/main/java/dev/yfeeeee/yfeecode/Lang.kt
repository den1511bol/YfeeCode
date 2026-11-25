// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

enum class Lang(val label: String) {
    PYTHON("Python"), JS("JavaScript"), HTML("HTML"), CSS("CSS"),
    CPP("C++"), CSHARP("C#"), TEXT("Text");

    companion object {
        fun fromName(name: String): Lang = when (name.substringAfterLast('.', "").lowercase()) {
            "py" -> PYTHON
            "js", "mjs" -> JS
            "html", "htm" -> HTML
            "css" -> CSS
            "cpp", "cc", "c", "h", "hpp" -> CPP
            "cs" -> CSHARP
            else -> TEXT
        }
    }
}
