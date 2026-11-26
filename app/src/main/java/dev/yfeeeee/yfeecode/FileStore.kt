// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class FileStore(private val ctx: Context) {
    val root: File = File(ctx.filesDir, "workspace").also { it.mkdirs() }
    val logFile: File = File(ctx.filesDir, "run.log")

    fun list(dir: File = root): List<File> =
        (dir.listFiles() ?: emptyArray()).sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

    fun read(f: File): String = runCatching { f.readText() }.getOrDefault("")

    fun write(f: File, text: String) {
        f.parentFile?.mkdirs()
        f.writeText(text)
    }

    /** Keeps only a plain file name: no folders, no "..", never empty. */
    private fun safeName(name: String): String =
        name.trim().replace('\\', '/').substringAfterLast('/').trim().ifBlank { "untitled" }.let { if (it == ".." || it == ".") "untitled" else it }

    fun create(dir: File, name: String, template: String = ""): File {
        val f = File(dir, safeName(name))
        if (!f.exists()) write(f, template)
        return f
    }

    fun mkdir(dir: File, name: String): File = File(dir, safeName(name)).also { it.mkdirs() }

    fun delete(f: File) { f.deleteRecursively() }

    /** Duplicates a file next to the original: name_copy.ext, name_copy2.ext ... Developer: @yfeeeee */
    fun duplicate(f: File): File? {
        if (!f.isFile) return null
        val base = f.nameWithoutExtension
        val ext = if (f.extension.isEmpty()) "" else "." + f.extension
        var n = 1
        var t = File(f.parentFile, base + "_copy" + ext)
        while (t.exists()) { n++; t = File(f.parentFile, base + "_copy" + n + ext) }
        return runCatching { f.copyTo(t) }.getOrNull()
    }

    /** Copies a file picked through the system picker into the workspace. */
    fun importUri(uri: Uri, dir: File): File? {
        var name = "imported.txt"
        ctx.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (i >= 0 && c.moveToFirst()) name = c.getString(i)
        }
        val out = File(dir, safeName(name))
        ctx.contentResolver.openInputStream(uri)?.use { input ->
            out.outputStream().use { input.copyTo(it) }
        } ?: return null
        return out
    }

    /** Packs the whole workspace into a zip. */
    fun zipTo(uri: Uri) {
        ctx.contentResolver.openOutputStream(uri)?.use { out ->
            java.util.zip.ZipOutputStream(out).use { z ->
                root.walkTopDown().filter { it.isFile }.forEach { f ->
                    z.putNextEntry(java.util.zip.ZipEntry(f.relativeTo(root).path.replace('\\', '/')))
                    f.inputStream().use { it.copyTo(z) }
                    z.closeEntry()
                }
            }
        }
    }

    /** Unpacks a zip into [dir]; entries trying to escape the folder are skipped. Returns file count. */
    fun unzipFrom(uri: Uri, dir: File): Int {
        var n = 0
        val base = dir.canonicalPath
        ctx.contentResolver.openInputStream(uri)?.use { input ->
            java.util.zip.ZipInputStream(input).use { z ->
                var e = z.nextEntry
                while (e != null) {
                    val target = File(dir, e.name).canonicalFile
                    if (target.path.startsWith(base + File.separator)) {
                        if (e.isDirectory) target.mkdirs() else {
                            target.parentFile?.mkdirs()
                            target.outputStream().use { z.copyTo(it) }
                            n++
                        }
                    }
                    e = z.nextEntry
                }
            }
        }
        return n
    }

    data class Hit(val file: File, val line: Int, val text: String)

    /** Case-insensitive text search through all workspace files (max 100 hits). */
    fun searchAll(q: String): List<Hit> {
        if (q.isBlank()) return emptyList()
        val out = mutableListOf<Hit>()
        for (f in root.walkTopDown().filter { it.isFile && it.length() < 500_000 }) {
            val lines = runCatching { f.readLines() }.getOrNull() ?: continue
            lines.forEachIndexed { i, l ->
                if (out.size < 100 && l.contains(q, ignoreCase = true)) out += Hit(f, i + 1, l.trim().take(80))
            }
            if (out.size >= 100) break
        }
        return out
    }

    fun exportTo(uri: Uri, f: File) {
        ctx.contentResolver.openOutputStream(uri)?.use { o -> f.inputStream().use { it.copyTo(o) } }
    }

    fun template(lang: Lang): String = when (lang) {
        Lang.PYTHON -> "import asyncio\n\n\nasync def main():\n    print(\"Hello from YfeeCode\")\n\n\nasyncio.run(main())\n"
        Lang.JS -> "console.log('Hello from YfeeCode');\n"
        Lang.HTML -> "<!DOCTYPE html>\n<html>\n<head>\n    <meta charset=\"utf-8\">\n    <title>Page</title>\n</head>\n<body>\n    \n</body>\n</html>\n"
        Lang.CPP -> "#include <iostream>\n\nint main() {\n    std::cout << \"Hello\" << std::endl;\n    return 0;\n}\n"
        Lang.CSHARP -> "using System;\n\nclass Program\n{\n    static void Main()\n    {\n        Console.WriteLine(\"Hello\");\n    }\n}\n"
        else -> ""
    }
}
