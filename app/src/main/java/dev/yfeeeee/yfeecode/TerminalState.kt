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
    /** Set by the terminal screen: opens a file in the editor (command "open"). */
    var openHook: ((File) -> Unit)? = null
    private var logOffset = 0L
    private var js: JsRunner? = null
    private val ui = android.os.Handler(android.os.Looper.getMainLooper())
    init { CloudRunner.prefetch(); Completer.loadUser(ctx) }

    fun print(s: String) { ui.post { s.split("\n").forEach { lines.add(it) }; trim() } }
    /** Grows on every print: the screen scrolls on it, because after 1500 lines the list size stops changing. */
    var scrollTick by mutableStateOf(0)
    private fun trim() { while (lines.size > 1500) lines.removeAt(0); scrollTick++ }

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
        if (mode == TermMode.PY) pySubmit(raw.trimEnd()) else try { shell(cmd) } catch (e: Throwable) { print("[error] " + e.message) }
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

    /** True when the command has no -flags: such commands are ours, with flags they go to the system sh. */
    private fun noFlags(cmd: String) = cmd.split(Regex("\\s+")).drop(1).none { it.startsWith("-") }

    private fun shell(cmd: String) {
        if (cmd.isEmpty()) return
        when {
            cmd == "clear" -> { lines.clear(); return }
            cmd == "help" -> { print("Команды: linux install / linux setup / linux dotnet (поставить Linux, инструменты, .NET), lpy файл.py, lpip install пакет, lx команда (выполнить в Linux), get URL (скачать файл из интернета), run файл, stop, log [N] (последние строки лога бота), sh/py (режим), mod файл.py .cmd (тест модуля), token ТОКЕН (сохранить в token.txt), clear + ls cd cat mkdir cp mv rm echo"); print("Ещё: status (состояние бота), log err / log find слово, autorestart on|off, backup (копия проектов), snip add имя текст / snip list / snip del имя (свои сниппеты), theme red|green|light, new telebot|aiogram|aiogram3|flask|hikka имя (новый проект), find слово (поиск по файлам), reqs файл.py (какие пакеты поставить), tokencheck (проверка token.txt), procfile [файл.py], gitignore, tree [папка] (дерево файлов), ver (версия), addcmd Модуль.py имя (новая команда Hikka), wc / head / tail файл [N], ip (внешний IP), diff файл1 файл2, open файл (открыть в редакторе), envinit (создать .env), grep слово файл, check файл.py (проверка синтаксиса)"); return }
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
            cmd.startsWith("find ") && noFlags(cmd) && !cmd.removePrefix("find ").trim().let { it.startsWith(".") || it.startsWith("/") } -> {
                // find слово — search text in all project files (up to 40 hits)
                val w = cmd.removePrefix("find ").trim()
                if (w.isEmpty()) { print("Использование: find слово"); return }
                var hits = 0
                store.root.walkTopDown().filter { it.isFile && it.length() < 500_000 && !it.path.contains("/.") }.forEach { f ->
                    if (hits >= 40) return@forEach
                    try {
                        f.readLines().forEachIndexed { n, l ->
                            if (hits < 40 && l.contains(w, ignoreCase = true)) { hits++; print(f.relativeTo(store.root).path + ":" + (n + 1) + ": " + l.trim().take(80)) }
                        }
                    } catch (_: Exception) {}
                }
                print(if (hits == 0) "Ничего не найдено" else "Найдено строк: " + hits + (if (hits >= 40) " (показаны первые 40)" else ""))
                return
            }
            cmd.startsWith("reqs ") -> {
                // reqs файл.py — which pip packages the file needs
                val f = File(cwd, cmd.removePrefix("reqs ").trim())
                if (!f.isFile) { print("Нет такого файла"); return }
                val std = setOf("os", "sys", "re", "json", "time", "asyncio", "random", "math", "datetime", "collections", "itertools", "functools",
                    "typing", "logging", "sqlite3", "pathlib", "subprocess", "threading", "io", "string", "base64", "hashlib", "html", "urllib",
                    "uuid", "copy", "traceback", "contextlib", "dataclasses", "enum", "shutil", "tempfile", "csv", "argparse", "inspect", "types",
                    "abc", "secrets", "textwrap", "zipfile", "struct", "socket", "ssl", "signal", "queue", "statistics", "decimal", "heapq", "operator")
                val alias = mapOf("telebot" to "pyTelegramBotAPI", "PIL" to "pillow", "bs4" to "beautifulsoup4", "yaml" to "pyyaml", "dotenv" to "python-dotenv", "cv2" to "opencv-python")
                val mods = Regex("^\\s*(?:from\\s+([A-Za-z_][A-Za-z0-9_]*)[\\w.]*\\s+import|import\\s+([A-Za-z_][A-Za-z0-9_]*))", RegexOption.MULTILINE)
                    .findAll(f.readText()).mapNotNull { it.groupValues[1].ifEmpty { it.groupValues[2] } }
                    .filter { it !in std && it != "loader" && it != "utils" }.distinct().toList()
                if (mods.isEmpty()) print("Сторонние пакеты не нужны")
                else { print("Поставь командой:"); print("pip install " + mods.joinToString(" ") { alias[it] ?: it }) }
                return
            }
            cmd == "tokencheck" -> {
                // tokencheck — checks the format of token.txt (the token itself is never printed)
                val f = File(cwd, "token.txt")
                if (!f.isFile) { print("Нет файла token.txt в этой папке. Создай его и вставь токен от @BotFather"); return }
                val t = f.readText().trim()
                if (Regex("\\d{6,12}:[A-Za-z0-9_-]{30,}").matches(t)) print("Формат токена верный (id бота начинается с " + t.take(4) + "…). Токен не показываю")
                else print("Формат токена неверный. Должно быть: цифры, двоеточие, длинная строка. Проверь пробелы, переносы и лишние кавычки")
                return
            }
            cmd == "procfile" || cmd.startsWith("procfile ") -> {
                // procfile [файл.py] — creates a Procfile for Heroku (worker: python файл.py)
                val name = cmd.removePrefix("procfile").trim().ifEmpty { "main.py" }
                val pf = File(cwd, "Procfile")
                if (pf.exists()) { print("Procfile уже есть:"); print(pf.readText().trim()); return }
                pf.writeText("worker: python " + name + "\n")
                print("Создан Procfile: worker: python " + name)
                return
            }
            cmd == "gitignore" -> {
                // gitignore — adds token.txt, .env and caches to .gitignore so secrets do not reach GitHub
                val gi = File(cwd, ".gitignore")
                val have = if (gi.exists()) gi.readLines().map { it.trim() }.toSet() else emptySet()
                val add = listOf("token.txt", ".env", "__pycache__/", "*.log", "*.session").filter { it !in have }
                if (add.isEmpty()) { print(".gitignore уже защищает токены"); return }
                gi.appendText((if (gi.exists() && gi.length() > 0 && !gi.readText().endsWith("\n")) "\n" else "") + add.joinToString("\n") + "\n")
                print("Добавлено в .gitignore: " + add.joinToString(", "))
                return
            }
            (cmd == "tree" || cmd.startsWith("tree ")) && noFlags(cmd) -> {
                // tree [папка] — file tree (depth up to 3, up to 80 lines)
                val sub = cmd.removePrefix("tree").trim()
                val base = (if (sub.isEmpty()) cwd else File(cwd, sub)).canonicalFile
                if (!base.isDirectory || !base.path.startsWith(store.root.canonicalPath)) { print("tree: нет такой папки"); return }
                var n = 0
                fun walk(d: File, depth: Int) {
                    val kids = (d.listFiles() ?: return).filter { !it.name.startsWith(".") }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
                    for (k in kids) {
                        if (n >= 80) return
                        n++
                        print("  ".repeat(depth) + (if (k.isDirectory) k.name + "/" else k.name))
                        if (k.isDirectory && depth < 2) walk(k, depth + 1)
                    }
                }
                print(base.name + "/")
                walk(base, 1)
                if (n >= 80) print("… (показаны первые 80)")
                return
            }
            cmd == "ver" -> {
                val v = try { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName } catch (_: Exception) { "?" }
                print("YfeeCode " + v + " (разработчик @yfeeeee)")
                print("Python: 3.13 (Chaquopy)")
                return
            }
            cmd.startsWith("addcmd ") -> {
                // addcmd модуль.py имя — appends a new command to a Hikka/Heroku module
                val p = cmd.removePrefix("addcmd ").trim().split(Regex("\\s+"))
                val f = File(cwd, p[0])
                val name = p.getOrNull(1)?.lowercase()?.takeIf { Regex("[a-z][a-z0-9_]*").matches(it) }
                if (!f.isFile || name == null) { print("Использование: addcmd MyMod.py имя (имя латиницей)"); return }
                if (Regex("def\\s+" + name + "cmd\\b").containsMatchIn(f.readText())) { print("Команда ." + name + " уже есть"); return }
                f.appendText("\n    @loader.command()\n    async def " + name + "cmd(self, message):\n        \"\"\"Описание команды\"\"\"\n        args = utils.get_args_raw(message)\n        await utils.answer(message, \"Команда " + name + ": \" + args)\n")
                print("Добавлена команда ." + name + " в " + f.name + ". Проверь: mod " + f.name + " ." + name + " привет")
                return
            }
            (cmd.startsWith("wc ") || cmd.startsWith("head ") || cmd.startsWith("tail ")) && noFlags(cmd) -> {
                // wc файл | head файл [N] | tail файл [N]
                val p = cmd.trim().split(Regex("\\s+"))
                val f = File(cwd, p.getOrNull(1) ?: "")
                if (!f.isFile) { print(p[0] + ": нет такого файла"); return }
                val text = f.readText()
                if (p[0] == "wc") {
                    print(text.lines().size.toString() + " строк, " + text.split(Regex("\\s+")).count { it.isNotEmpty() } + " слов, " + f.length() + " байт")
                } else {
                    val n = (p.getOrNull(2)?.toIntOrNull() ?: 10).coerceIn(1, 500)
                    val ls = text.lines()
                    (if (p[0] == "head") ls.take(n) else ls.takeLast(n)).forEach { print(it) }
                }
                return
            }
            cmd == "ip" -> {
                // ip — external IP address (useful for checking the network and proxies)
                print("Спрашиваю внешний IP…")
                thread {
                    try {
                        val c = java.net.URL("https://api.ipify.org").openConnection() as java.net.HttpURLConnection
                        c.connectTimeout = 10000; c.readTimeout = 10000
                        print("Внешний IP: " + c.inputStream.bufferedReader().readText().trim())
                    } catch (e: Exception) { print("Не вышло: " + e.message) }
                }
                return
            }
            cmd.startsWith("diff ") && noFlags(cmd) -> {
                // diff файл1 файл2 — first 40 differing lines
                val p = cmd.removePrefix("diff ").trim().split(Regex("\\s+"))
                val a = File(cwd, p.getOrNull(0) ?: ""); val b = File(cwd, p.getOrNull(1) ?: "")
                if (p.size < 2 || !a.isFile || !b.isFile) { print("Использование: diff файл1 файл2"); return }
                val la = a.readLines(); val lb = b.readLines()
                var shown = 0
                for (i in 0 until maxOf(la.size, lb.size)) {
                    val x = la.getOrNull(i); val y = lb.getOrNull(i)
                    if (x != y) {
                        if (shown >= 40) { print("… (показаны первые 40 различий)"); break }
                        shown++
                        if (x != null) print((i + 1).toString() + " - " + x)
                        if (y != null) print((i + 1).toString() + " + " + y)
                    }
                }
                if (shown == 0) print("Файлы одинаковые")
                return
            }
            cmd.startsWith("open ") -> {
                val f = File(cwd, cmd.removePrefix("open ").trim())
                if (!f.isFile) { print("open: нет такого файла"); return }
                val h = openHook
                if (h == null) print("open: редактор сейчас недоступен") else { h(f); print("Открываю " + f.name + " в редакторе") }
                return
            }
            cmd == "envinit" -> {
                // envinit — creates .env (BOT_TOKEN, ADMIN_ID) and protects it in .gitignore
                val env = File(cwd, ".env")
                if (env.exists()) print(".env уже есть") else { env.writeText("BOT_TOKEN=\nADMIN_ID=\n"); print("Создан .env: впиши BOT_TOKEN и ADMIN_ID") }
                val gi = File(cwd, ".gitignore")
                if (!gi.exists() || ".env" !in gi.readLines().map { it.trim() }) {
                    gi.appendText((if (gi.exists() && gi.length() > 0 && !gi.readText().endsWith("\n")) "\n" else "") + ".env\n")
                    print(".env добавлен в .gitignore, на GitHub он не попадёт")
                }
                return
            }
            cmd.startsWith("grep ") && noFlags(cmd) -> {
                // grep слово файл — lines of the file containing the word (up to 40)
                val p = cmd.removePrefix("grep ").trim().split(Regex("\\s+"))
                val f = File(cwd, p.getOrNull(1) ?: "")
                if (p.size < 2 || !f.isFile) { print("Использование: grep слово файл"); return }
                var n = 0
                f.readLines().forEachIndexed { i, l ->
                    if (n < 40 && l.contains(p[0], ignoreCase = true)) { n++; print((i + 1).toString() + ": " + l.trim().take(100)) }
                }
                if (n == 0) print("Не найдено")
                return
            }
            cmd.startsWith("check ") -> {
                // check файл.py — syntax check with Russian hints (same as the editor)
                val f = File(cwd, cmd.removePrefix("check ").trim())
                if (!f.isFile) { print("check: нет такого файла"); return }
                if (f.extension != "py") { print("check: пока только для .py"); return }
                thread {
                    val r = try { Python.getInstance().getModule("syntax_check").callAttr("check", f.readText()).toString() } catch (e: Throwable) { "Не удалось проверить: " + e.message }
                    print(if (r.isBlank()) "Синтаксис в порядке" else if ('|' in r) "Строка " + r.substringBefore('|') + ": " + r.substringAfter('|') else r)
                }
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
