// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

/** Finds functions, classes and selectors of a file for the quick jump list. */
object Symbols {
    private val py = Regex("""^\s*(?:async\s+)?(def|class)\s+(\w+)""")
    private val js1 = Regex("""^\s*(?:export\s+)?(?:default\s+)?(?:async\s+)?(function|class)\s*\*?\s*(\w+)""")
    private val js2 = Regex("""^\s*(?:export\s+)?(?:const|let|var)\s+(\w+)\s*=\s*(?:async\s*)?(?:\([^)]*\)|\w+)\s*=>""")
    private val cType = Regex("""^\s*(?:public\s+|private\s+|protected\s+|internal\s+|static\s+|sealed\s+|abstract\s+|partial\s+)*(class|struct|interface|enum|namespace)\s+(\w+)""")
    private val cFun = Regex("""^\s*(?:[\w:<>,*&\[\]]+\s+)+([A-Za-z_]\w*)\s*\([^;]*\)\s*(?:const\s*)?(?:override\s*)?\{?\s*""")
    private val css = Regex("""^\s*([^\s@}/*][^{;]*)\{""")
    private val skip = setOf("return", "else", "new", "delete", "throw", "case", "using", "await", "yield", "if", "for", "while", "switch", "catch", "foreach", "lock")

    fun list(text: String, lang: Lang): List<Pair<Int, String>> {
        val out = ArrayList<Pair<Int, String>>()
        val lines = text.split("\n")
        for ((i, l) in lines.withIndex()) {
            if (out.size >= 500) break
            val label: String? = when (lang) {
                Lang.PYTHON -> py.find(l)?.let { "${it.groupValues[1]} ${it.groupValues[2]}" }
                Lang.JS -> js1.find(l)?.let { "${it.groupValues[1]} ${it.groupValues[2]}" }
                    ?: js2.find(l)?.let { "function ${it.groupValues[1]}" }
                Lang.CPP, Lang.CSHARP -> cType.find(l)?.let { "${it.groupValues[1]} ${it.groupValues[2]}" }
                    ?: cFun.find(l)?.takeIf { l.trim().substringBefore(' ') !in skip && it.groupValues[1] !in skip }?.let { "fn ${it.groupValues[1]}" }
                Lang.CSS -> css.find(l)?.let { it.groupValues[1].trim() }
                else -> null
            }
            if (label != null) out.add((i + 1) to label)
        }
        return out
    }
}
