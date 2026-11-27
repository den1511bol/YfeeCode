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
    init { CloudRunner.prefetch(); Completer.loadUser(ctx) }

    fun print(s: String) { ui.post { s.split("\n").forEach { lines.add(it) }; trim() } }
    private fun trim() { while (lines.size > 1500) lines.removeAt(0) }

    val prompt: String
        get() = if (mode == TermMode.PY) (if (pyMore) "... " else ">>> ")
        else {
            val rel = cwd.relativeToOrSelf(store.root).path
            (if (rel.startsWith("..")) "~/" + cwd.name else rel.ifEmpty { "~" }) + " $ "
        }

    @Volatile private var proc: Process? = null

    fun submit(raw: String) {
        // a Linux program is running: send the line to its stdin
        val rp = proc
        if (rp != null && rp.isAlive) {
            lines.add(raw)
            Thread { try { rp.outputStream.write((raw + "\n").toByteArray()); rp.outputStream.flush() } catch (_: Exception) {} }.start()
            return
        }
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
            cmd == "help" -> { print("Команды: linux install / linux setup / linux dotnet (поставить Linux, инструменты, .NET), lpy файл.py, lpip install пакет, lx команда (выполнить в Linux), get URL (скачать файл из интернета), run файл, stop, log [N] (последние строки лога бота), sh/py (режим), mod файл.py .cmd (тест модуля), token ТОКЕН (сохранить в token.txt), clear + ls cd cat mkdir cp mv rm echo"); return }
            cmd == "linux" || cmd == "linux install" -> {
                if (cmd == "linux" && Linux.installed(ctx)) { print("Linux установлен. Команды выполняй так: lx команда (например: lx apk add git)"); return }
                thread {
                    try { Linux.install(ctx) { print(it) } }
                    catch (e: Exception) { print("Ошибка установки: ${e.message}") }
                }
                return
            }
            cmd == "linux dotnet" -> {
                // .NET SDK from the Alpine repository: lets the play button run C# offline
                thread {
                    try { Linux.exec(ctx, store, cwd, "apk add --no-cache dotnet8-sdk dotnet8-targeting-pack icu-libs", { proc = it }) { print(it) }
                        print(if (Linux.hasDotnet(ctx)) "Готово: .NET установлен, кнопка ▶ на .cs теперь работает офлайн" else "Не удалось поставить .NET (пакет dotnet8-sdk недоступен)") }
                    catch (e: Exception) { print("Ошибка: ${e.message}") }
                }
                return
            }
            cmd == "linux setup" -> {
                // one-shot install of the usual tools inside Alpine
                thread {
                    try { Linux.exec(ctx, store, cwd, "apk add --no-cache python3 py3-pip git build-base nano vim curl wget openssh tmux nodejs npm bash htop zip unzip jq", { proc = it }) { print(it) }; print("Готово: python3, pip, git, g++, nano, vim, curl, wget, ssh, tmux, node, npm, bash. Есть pkg install, apt install") }
                    catch (e: Exception) { print("Ошибка: ${e.message}") }
                }
                return
            }
            cmd.startsWith("lpy ") || cmd.startsWith("lpip ") -> {
                // lpy file.py [args]  — run Python inside Linux;  lpip install X  — pip inside Linux
                val c = if (cmd.startsWith("lpy ")) "python3 " + cmd.removePrefix("lpy ").trim()
                        else if (cmd.removePrefix("lpip ").trim().startsWith("install")) "pip " + cmd.removePrefix("lpip ").trim() + " --break-system-packages"
                        else "pip " + cmd.removePrefix("lpip ").trim()
                thread {
                    try { Linux.exec(ctx, store, cwd, c, { proc = it }) { print(it) } }
                    catch (e: Exception) { print("Ошибка: ${e.message}") }
                }
                return
            }
            cmd == "lx" -> { print("Использование: lx команда (например: lx apk add git)"); return }
            cmd.startsWith("lx ") -> {
                val c = cmd.removePrefix("lx ").trim()
                thread {
                    try { Linux.exec(ctx, store, cwd, c, { proc = it }) { print(it) } }
                    catch (e: Exception) { print("Ошибка: ${e.message}") }
                }
                return
            }
            Regex("^(print\\(|import \\w+|from \\w+ import |def \\w+|async def |class \\w+).*").matches(cmd) -> {
                print("Это похоже на Python. Набери py (или нажми кнопку py вверху), потом вводи код. Назад: sh")
                return
            }
            cmd == "py" -> { mode = TermMode.PY; return }
            cmd == "sh" -> { mode = TermMode.SH; return }
            cmd == "env" || cmd.startsWith("env ") -> {
                // env | env set KEY значение | env del KEY  — переменные бота в файле .env (Developer: @yfeeeee)
                val envF = File(cwd, ".env")
                val parts = cmd.removePrefix("env").trim().split(Regex("\\s+"), 3)
                val sub = parts.getOrNull(0).orEmpty()
                val lines0 = if (envF.exists()) envF.readLines().filter { it.isNotBlank() } else emptyList()
                when (sub) {
                    "", "list" -> print(if (lines0.isEmpty()) "Файл .env пуст. Добавь: env set BOT_TOKEN 123:ABC" else lines0.joinToString("\n") { l -> val i = l.indexOf('='); if (i > 0) l.substring(0, i + 1) + "***" else l })
                    "set" -> {
                        val key = parts.getOrNull(1).orEmpty()
                        val v = parts.getOrNull(2).orEmpty()
                        if (key.isEmpty() || v.isEmpty()) print("Использование: env set КЛЮЧ значение")
                        else {
                            envF.writeText((lines0.filter { !it.startsWith("$key=") } + "$key=$v").joinToString("\n") + "\n")
                            print("Сохранено в .env: $key. В боте: os.environ[\"$key\"]. Не заливай .env в git!")
                        }
                    }
                    "del" -> {
                        val key = parts.getOrNull(1).orEmpty()
                        envF.writeText(lines0.filter { !it.startsWith("$key=") }.joinToString("\n") + "\n")
                        print("Удалено: $key")
                    }
                    else -> print("Использование: env | env set КЛЮЧ значение | env del КЛЮЧ")
                }
                return
            }
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
                if (line.isBlank()) listModuleCommands(File(cwd, name))
                else runModuleCommand(File(cwd, name), line)
                return
            }
            cmd.startsWith("get ") -> {
                // get <url> [имя] — download a file from the internet (for example from GitHub) into the current folder
                val parts = cmd.removePrefix("get ").trim().split(Regex("\\s+"))
                val url = parts[0]
                if (!url.startsWith("http://") && !url.startsWith("https://")) { print("Использование: get https://адрес/файл [имя]"); return }
                val name = (parts.getOrNull(1) ?: url.substringBefore('?').substringAfterLast('/')).substringAfterLast('/').ifBlank { "download.txt" }
                val dest = File(cwd, name)
                print("Скачиваю $url …")
                thread {
                    try {
                        val c = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                        c.connectTimeout = 15000; c.readTimeout = 30000; c.instanceFollowRedirects = true
                        c.setRequestProperty("User-Agent", "YfeeCode")
                        if (c.responseCode != 200) { print("Ошибка: HTTP ${c.responseCode}"); return@thread }
                        val n = c.inputStream.use { i -> dest.outputStream().use { o -> i.copyTo(o) } }
                        print("Сохранено: ${dest.name} ($n байт)")
                    } catch (e: Exception) { print("Ошибка: ${e.message}") }
                }
                return
            }
            cmd == "log" || cmd.startsWith("log ") -> {
                // log [N] | log err | log find слово — show the bot log, only errors, or lines with a word
                val arg = cmd.removePrefix("log").trim()
                val f = store.logFile
                if (!f.exists() || f.length() == 0L) { print("Лог пуст: запусти бота кнопкой ▶ или командой run файл.py"); return }
                val all = f.readText().takeLast(200000).trimEnd('\n').split("\n")
                val lines = when {
                    arg == "err" -> all.filter { l -> val t = l.lowercase(); "error" in t || "traceback" in t || "exception" in t || "ошибк" in t }
                    arg.startsWith("find ") -> arg.removePrefix("find ").trim().let { w -> all.filter { it.contains(w, ignoreCase = true) } }
                    else -> all
                }
                val n = arg.toIntOrNull()?.coerceIn(1, 500) ?: 30
                if (lines.isEmpty()) print("Ничего не найдено. Подсказка: log 50, log err, log find слово")
                else print(lines.takeLast(n).joinToString("\n"))
                return
            }
            cmd == "autorestart" || cmd.startsWith("autorestart ") -> {
                // autorestart on|off — restart the bot after a crash (up to 5 times)
                val sp = ctx.getSharedPreferences("bot_state", Context.MODE_PRIVATE)
                when (cmd.removePrefix("autorestart").trim()) {
                    "on" -> sp.edit().putBoolean("autorestart", true).apply()
                    "off" -> sp.edit().putBoolean("autorestart", false).apply()
                }
                print("Автоперезапуск после падения: " + if (sp.getBoolean("autorestart", true)) "включён" else "выключен (autorestart on / off)")
                return
            }
            cmd == "backup" -> {
                // backup — zip all projects into the app's external folder (Android/data/<app>/files)
                try {
                    val dir = ctx.getExternalFilesDir(null) ?: ctx.filesDir
                    val name = "yfeecode-backup-" + java.text.SimpleDateFormat("yyyyMMdd-HHmm", java.util.Locale.US).format(java.util.Date()) + ".zip"
                    val out = File(dir, name)
                    store.zipTo(android.net.Uri.fromFile(out))
                    print("Копия готова: " + out.absolutePath + " (" + out.length() / 1024 + " КБ). Скачать zip можно кнопкой ZIP↑ в Файлах.")
                } catch (e: Exception) { print("Не вышло: " + e.message) }
                return
            }
            cmd == "snip" || cmd.startsWith("snip ") -> {
                // snip add имя текст (\\n = новая строка, | = курсор) | snip list | snip del имя
                val a = cmd.removePrefix("snip").trim()
                val name = a.substringAfter(' ').substringBefore(' ')
                when {
                    a.startsWith("add ") && a.split(' ').size >= 3 -> {
                        val text = a.removePrefix("add ").substringAfter(' ').replace("\\n", "\n")
                        Completer.user[name] = text; Completer.saveUser(ctx)
                        print("Сниппет сохранён: набери " + name + " в редакторе и нажми подсказку ⚡")
                    }
                    a.startsWith("del ") -> {
                        if (Completer.user.remove(name) != null) { Completer.saveUser(ctx); print("Удалён: " + name) } else print("Нет такого сниппета")
                    }
                    a == "list" -> print(if (Completer.user.isEmpty()) "Своих сниппетов нет" else Completer.user.keys.joinToString("\n") { "• " + it })
                    else -> print("snip add имя текст | snip list | snip del имя. В тексте: \\n = новая строка, | = где встанет курсор")
                }
                return
            }
            cmd == "theme" || cmd.startsWith("theme ") -> {
                // theme red|green|light — terminal colors
                val n = listOf("red", "green", "light").indexOf(cmd.removePrefix("theme").trim())
                if (n >= 0) { Settings.termTheme = n; Settings.save(ctx); print("Тема терминала включена") }
                else print("Использование: theme red | theme green | theme light")
                return
            }
            cmd == "new" || cmd.startsWith("new ") -> {
                // new telebot|aiogram|aiogram3|flask|hikka имя — project folder with main file, requirements, Procfile, .gitignore
                val p = cmd.removePrefix("new").trim().split(Regex("\\s+"))
                val kinds = mapOf("telebot" to 0, "aiogram" to 1, "aiogram3" to 4, "flask" to 5, "hikka" to 3)
                val idx = kinds[p.getOrNull(0)]
                val name = p.getOrNull(1)?.takeIf { Regex("[A-Za-z0-9_-]+").matches(it) }
                if (idx == null || name == null) { print("Использование: new telebot|aiogram|aiogram3|flask|hikka имя_проекта"); return }
                val dir = File(cwd, name)
                if (dir.exists()) { print("Папка уже есть: " + name); return }
                dir.mkdirs()
                val t = Templates.all[idx]
                val main = if (idx == 3) t.fileName else "main.py"
                File(dir, main).writeText(t.body)
                val req = mapOf(0 to "pyTelegramBotAPI", 1 to "aiogram==2.25.1", 4 to "aiogram>=3\npython-dotenv", 5 to "flask\ngunicorn", 3 to "")[idx] ?: ""
                if (idx != 3) {
                    File(dir, "requirements.txt").writeText(req + "\n")
                    File(dir, "Procfile").writeText(if (idx == 5) "web: gunicorn main:app\n" else "worker: python main.py\n")
                }
                File(dir, ".gitignore").writeText("token.txt\n.env\n__pycache__/\n*.log\n")
                print("Проект создан: " + name + " (файл " + main + "). Токен положи в token.txt. Войти: cd " + name)
                return
            }
            cmd == "status" || cmd == "bots" -> {
                // status — what the background bot is doing
                val sp = ctx.getSharedPreferences("bot_state", Context.MODE_PRIVATE)
                val last = store.logFile.takeIf { it.exists() && it.length() > 0 }?.readText()?.takeLast(20000)?.trimEnd('\n')?.split("\n")?.takeLast(3)
                val done = last?.lastOrNull()?.startsWith("[done]") == true
                print("Бот: " + (if (sp.getBoolean("running", false) && !done) "работает" else "остановлен") +
                    (sp.getString("script", null)?.let { " (" + File(it).name + ")" } ?: ""))
                print("Автоперезапуск: " + if (sp.getBoolean("autorestart", true)) "включён" else "выключен")
                if (last != null) { print("Последние строки лога:"); last.forEach { print("  " + it) } }
                else print("Лог пуст. Запусти бота кнопкой ▶ или командой run файл.py")
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
                proc = p // «stop» теперь останавливает и обычные команды
                p.inputStream.bufferedReader().forEachLine { print(it) }
                p.waitFor()
            } catch (e: Exception) { print("Ошибка: ${e.message}") }
        }
    }

    /** Runs a Python file inside Alpine (pip libraries from lpip, input() works). */
    fun runLinuxPython(f: File) {
        if (!Linux.installed(ctx)) { print("Linux не установлен. Выполни: linux install, затем linux setup"); return }
        print("Запуск ${f.name} в Linux (python3)...")
        thread {
            try {
                val q = "'" + f.name.replace("'", "'\\''") + "'"
                Linux.exec(ctx, store, f.parentFile ?: cwd, "python3 -u $q", { proc = it }) { print(it) }
            } catch (e: Exception) { print("Ошибка: ${e.message}") }
        }
    }

    fun runFile(f: File) {
        if (!f.exists()) { print("Нет файла: ${f.name}"); return }
        when (Lang.fromName(f.name)) {
            Lang.PYTHON -> {
                ui.post {
                    if (Battery.requestIfNeeded(ctx)) print("Разреши работу в фоне в окне Android, чтобы бот не засыпал при выключенном экране")
                }
                BotService.stop(ctx) // kills previous run (if any)
                thread {
                    Thread.sleep(800) // let the old :bot process die first
                    store.logFile.writeText("")
                    logOffset = 0
                    BotService.start(ctx, f.absolutePath, store.logFile.absolutePath)
                    tailLog()
                    Thread.sleep(10000)
                    if (store.logFile.length() == 0L) print("Бот не ответил за 10 секунд. Проверь: уведомления Android разрешены, работа в фоне разрешена, в списке уведомлений есть «Бот». Попробуй run на простом файле print(\"hi\").")
                }
            }
            Lang.JS -> ui.post {
                js?.stop()
                js = JsRunner(ctx) { print(it) }.also { it.run(f.readText()) }
            }
            Lang.CPP, Lang.CSHARP -> {
                val lang = Lang.fromName(f.name)
                if (lang == Lang.CSHARP && Linux.installed(ctx) && Linux.hasDotnet(ctx)) {
                    print("Сборка ${f.name} (.NET, офлайн), первый запуск может занять до минуты...")
                    thread {
                        try {
                            val q = "'" + f.name.replace("'", "'\\''") + "'"
                            val cmdLine = "yfeecs $q"
                            Linux.exec(ctx, store, f.parentFile ?: cwd, cmdLine, { proc = it }) { print(it) }
                        } catch (e: Exception) { print("Ошибка: ${e.message}") }
                    }
                    return
                }
                if (lang == Lang.CPP && Linux.installed(ctx) && Linux.hasGpp(ctx)) {
                    print("Компиляция ${f.name} (g++, офлайн)...")
                    thread {
                        try {
                            val q = "'" + f.name.replace("'", "'\\''") + "'"
                            Linux.exec(ctx, store, f.parentFile ?: cwd, "g++ -std=gnu++17 -O1 $q -o /tmp/yfee_out && /tmp/yfee_out", { proc = it }) { print(it) }
                        } catch (e: Exception) { print("Ошибка: ${e.message}") }
                    }
                    return
                }
                print("Компиляция ${f.name} (${lang.label}, онлайн)... ${if (lang == Lang.CSHARP) "C# может занять до 30 секунд" else ""}")
                thread {
                    try { print(CloudRunner.run(lang, f.readText())) }
                    catch (e: Exception) { print("Ошибка: нужен интернет (${e.message})") }
                }
            }
            else -> print("Запуск для ${f.extension} не поддерживается (HTML открывается кнопкой «Просмотр»)")
        }
    }

    /** Prints the commands found in a Heroku/Hikka-style module file. Developer: @yfeeeee */
    fun listModuleCommands(f: File) {
        if (!f.exists()) { print("Нет файла: ${f.name}"); return }
        thread {
            try {
                val r = Python.getInstance().getModule("hikka_sandbox").callAttr("commands", f.absolutePath).asList()
                if (r.isEmpty()) print("В модуле нет команд")
                else print("Команды модуля: " + r.joinToString(", ") { "." + it.toString() } + "\nПроверка: mod " + f.name + " ." + r[0].toString() + " аргументы")
            } catch (e: Throwable) { print("[error] " + e.message) }
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
        proc?.destroyForcibly(); proc = null
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
