// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.chaquo.python.Python
import java.io.File
import kotlin.concurrent.thread

enum class TermMode(val label: String) { SH("sh"), PY("py") }

class TerminalState(private val ctx: Context, private val store: FileStore) {
    val lines = mutableStateListOf<String>()
    var mode by mutableStateOf(TermMode.SH)
    var cwd: File by mutableStateOf(store.root)
    var pyMore by mutableStateOf(false)
    private var logOffset = 0L
    private var js: JsRunner? = null
    private val ui = android.os.Handler(android.os.Looper.getMainLooper())

    fun print(s: String) { ui.post { s.split("\n").forEach { lines.add(it) }; trim() } }
    private fun trim() { while (lines.size > 1500) lines.removeAt(0) }

    val prompt: String
        get() = if (mode == TermMode.PY) (if (pyMore) "... " else ">>> ")
        else cwd.relativeToOrSelf(store.root).path.ifEmpty { "~" } + " $ "

    fun submit(raw: String) {
        val cmd = AutoFix.command(raw)
        if (cmd != raw.trim()) print("(исправлено: $cmd)")
        lines.add(prompt + if (cmd.startsWith("token ")) "token ***" else cmd)
        if (mode == TermMode.PY) pySubmit(raw.trimEnd()) else shell(cmd)
    }

    private fun pySubmit(line: String) {
        thread {
            try {
                val r = Python.getInstance().getModule("repl").callAttr("feed", line).asList()
                val out = r[0].toString().trimEnd('\n')
                if (out.isNotEmpty()) print(out)
                ui.post { pyMore = r[1].toBoolean() }
            } catch (e: Throwable) { print("[error] " + e.message) }
        }
    }

    private fun shell(cmd: String) {
        if (cmd.isEmpty()) return
        when {
            cmd == "clear" -> { lines.clear(); return }
            cmd == "help" -> { print("Команды: run файл, stop, sh/py (режим), mod файл.py .cmd (тест модуля), token ТОКЕН (сохранить в token.txt), clear + ls cd cat mkdir cp mv rm echo"); return }
            cmd == "py" -> { mode = TermMode.PY; return }
            cmd == "sh" -> { mode = TermMode.SH; return }
            cmd.startsWith("token ") -> {
                // token 123:ABC — saves the bot token to token.txt in the current folder
                File(cwd, "token.txt").writeText(cmd.removePrefix("token ").trim())
                print("token.txt сохранён в текущей папке")
                return
            }
            cmd.startsWith("mod ") -> {
                // mod <file.py> .command args  — test a Heroku/Hikka module offline
                val rest = cmd.removePrefix("mod ").trim()
                val name = rest.substringBefore(' ')
                val line = rest.substringAfter(' ', "")
                if (line.isBlank()) print("Использование: mod файл.py .команда аргументы")
                else runModuleCommand(File(cwd, name), line)
                return
            }
            cmd == "stop" -> { stopAll(); return }
            cmd.startsWith("run ") -> { runFile(File(cwd, cmd.removePrefix("run ").trim())); return }
            cmd == "cd" || cmd.startsWith("cd ") -> {
                val t = cmd.removePrefix("cd").trim()
                val d = if (t.isEmpty() || t == "~") store.root else File(cwd, t).canonicalFile
                if (d.isDirectory && d.path.startsWith(store.root.canonicalPath)) cwd = d
                else print("cd: нет такой папки")
                return
            }
        }
        thread {
            try {
                val p = ProcessBuilder("sh", "-c", cmd).directory(cwd).redirectErrorStream(true).start()
                p.inputStream.bufferedReader().forEachLine { print(it) }
                p.waitFor()
            } catch (e: Exception) { print("Ошибка: ${e.message}") }
        }
    }

    fun runFile(f: File) {
        if (!f.exists()) { print("Нет файла: ${f.name}"); return }
        when (Lang.fromName(f.name)) {
            Lang.PYTHON -> {
                BotService.stop(ctx) // kills previous run (if any)
                thread {
                    Thread.sleep(800) // let the old :bot process die first
                    store.logFile.writeText("")
                    logOffset = 0
                    BotService.start(ctx, f.absolutePath, store.logFile.absolutePath)
                    tailLog()
                }
            }
            Lang.JS -> ui.post {
                js?.stop()
                js = JsRunner(ctx) { print(it) }.also { it.run(f.readText()) }
            }
            else -> print("Запуск для ${f.extension} не поддерживается (HTML открывается кнопкой «Просмотр»)")
        }
    }

    /** Test a Heroku/Hikka-style module command, e.g. ".hello world". */
    fun runModuleCommand(f: File, line: String) {
        thread {
            try {
                val out = Python.getInstance().getModule("hikka_sandbox").callAttr("call", f.absolutePath, line).toString()
                print(out.trimEnd('\n'))
            } catch (e: Throwable) { print("[error] " + e.message) }
        }
    }

    fun stopAll() {
        tailGen++
        BotService.stop(ctx)
        ui.post { js?.stop(); js = null }
        print("[stop]")
    }

    @Volatile private var tailGen = 0

    private fun tailLog() {
        val gen = ++tailGen
        thread {
            var idle = 0
            while (idle < 14400 && gen == tailGen) {
                val f = store.logFile
                if (f.length() > logOffset) {
                    f.inputStream().use {
                        it.skip(logOffset)
                        val t = it.readBytes()
                        logOffset += t.size
                        print(String(t).trimEnd('\n'))
                    }
                    idle = 0
                } else idle++
                Thread.sleep(250)
            }
        }
    }
}
