// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Compiles and runs C++ / C# through the free Wandbox API (needs internet). */
object CloudRunner {
    private const val BASE = "https://wandbox.org/api/"
    private val cache = HashMap<Lang, String>()

    private fun http(url: String, body: String? = null): String {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 15000
        c.readTimeout = 60000
        c.setRequestProperty("User-Agent", "YfeeCode/1.0 (@yfeeeee)")
        if (body != null) {
            c.requestMethod = "POST"
            c.doOutput = true
            c.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            c.outputStream.use { it.write(body.toByteArray()) }
        }
        val s = if (c.responseCode in 200..299) c.inputStream else (c.errorStream ?: c.inputStream)
        return s.bufferedReader().use { it.readText() }
    }

    private fun compiler(lang: Lang): String {
        cache[lang]?.let { return it }
        val arr = JSONArray(http(BASE + "list.json"))
        val want = if (lang == Lang.CPP) "C++" else "C#"
        var best: String? = null
        var mono: String? = null
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (o.optString("language") != want) continue
            val n = o.optString("name")
            if (lang == Lang.CPP) {
                if (n == "gcc-head") { best = n; break }
                if (n.startsWith("gcc-") && !n.contains("c-")) best = n
            } else if (n.startsWith("mono")) mono = n // mono compiles much faster than dotnet
            else if (n.startsWith("dotnet")) best = n
        }
        if (lang == Lang.CSHARP && best == null) best = mono // dotnet preferred: mono breaks Cyrillic output
        val r = best ?: throw IllegalStateException("нет компилятора для $want")
        cache[lang] = r
        return r
    }

    /** Warm up: fetch compiler names in background so the first run is faster. */
    fun prefetch() {
        Thread { try { compiler(Lang.CPP); compiler(Lang.CSHARP) } catch (_: Exception) {} }.start()
    }

    /** Returns output text; throws on network errors. */
    fun run(lang: Lang, code: String, stdin: String = ""): String {
        val o = JSONObject()
        o.put("code", code)
        o.put("compiler", compiler(lang))
        o.put("stdin", stdin)
        if (lang == Lang.CPP) o.put("compiler-option-raw", "-std=gnu++17")
        val r = JSONObject(http(BASE + "compile.json", o.toString()))
        val sb = StringBuilder()
        val ce = r.optString("compiler_error")
        if (ce.isNotBlank()) sb.append(ce.trimEnd()).append('\n')
        val po = r.optString("program_output")
        if (po.isNotEmpty()) sb.append(po.trimEnd('\n')).append('\n')
        val pe = r.optString("program_error")
        if (pe.isNotBlank()) sb.append(pe.trimEnd()).append('\n')
        val st = r.optString("status")
        if (st.isNotEmpty() && st != "0") sb.append("[код выхода $st]")
        else if (sb.isEmpty()) sb.append("[готово, вывода нет]")
        return sb.toString().trimEnd('\n')
    }
}
