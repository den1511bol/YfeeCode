// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import android.system.Os
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/** Alpine Linux userland on the phone through proot (packed by CI as native libraries). */
object Linux {
    private const val ROOTFS_URL = "https://dl-cdn.alpinelinux.org/alpine/v3.20/releases/aarch64/alpine-minirootfs-3.20.3-aarch64.tar.gz"

    private fun base(ctx: Context) = File(ctx.filesDir, "linux")
    private fun root(ctx: Context) = File(base(ctx), "alpine")
    private fun nat(ctx: Context) = ctx.applicationInfo.nativeLibraryDir

    fun hasProot(ctx: Context) = File(nat(ctx), "libproot.so").exists()
    fun installed(ctx: Context) = File(root(ctx), "bin/busybox").exists()

    fun install(ctx: Context, say: (String) -> Unit) {
        if (!hasProot(ctx)) { say("proot не найден в этой сборке (устройство не arm64 или сборка без proot)"); return }
        val tgz = File(base(ctx), "alpine.tar.gz")
        base(ctx).mkdirs()
        say("Скачиваю Alpine Linux…")
        val c = URL(ROOTFS_URL).openConnection() as HttpURLConnection
        c.connectTimeout = 20000; c.readTimeout = 60000
        if (c.responseCode != 200) { say("Ошибка загрузки: HTTP ${c.responseCode}"); return }
        c.inputStream.use { i -> tgz.outputStream().use { o -> i.copyTo(o) } }
        say("Скачано ${tgz.length() / 1024} КБ, распаковываю…")
        root(ctx).deleteRecursively()
        root(ctx).mkdirs()
        val p = ProcessBuilder("tar", "-xzf", tgz.absolutePath, "-C", root(ctx).absolutePath)
            .redirectErrorStream(true).start()
        p.inputStream.bufferedReader().forEachLine { say(it) }
        p.waitFor()
        tgz.delete()
        if (!installed(ctx)) { say("Распаковка не удалась"); return }
        File(root(ctx), "etc/resolv.conf").writeText("nameserver 8.8.8.8\nnameserver 1.1.1.1\n")
        say("Готово. Попробуй: lx uname -a   или   lx apk add python3 git")
    }

    fun prepareLibs(ctx: Context): File {
        val libs = File(base(ctx), "lib").also { it.mkdirs() }
        // the app path changes after every update, so always recreate the links
        val target = File(nat(ctx), "libtalloc.so").absolutePath
        val link = File(libs, "libtalloc.so.2")
        val cur = try { Os.readlink(link.absolutePath) } catch (_: Exception) { null }
        if (cur != target) {
            try { link.delete() } catch (_: Exception) {}
            try { Os.symlink(target, link.absolutePath) } catch (_: Exception) {}
        }
        return libs
    }

    /** Runs a command inside Alpine; [cwd] is a folder inside the project root (mounted at /work). */
    /** Command line + environment for an interactive shell inside Alpine (used by the PTY terminal). */
    fun shell(ctx: Context, store: FileStore): Pair<Array<String>, Array<String>> {
        val libs = prepareLibs(ctx)
        installScripts(ctx)
        File(root(ctx), "sdcard").mkdirs()
        val sh = "if [ -x /usr/bin/bash ]; then exec /usr/bin/bash -l; else exec /bin/sh -l; fi"
        val args = arrayOf(
            File(nat(ctx), "libproot.so").absolutePath, "-0", "--link2symlink",
            "-r", root(ctx).absolutePath,
            "-b", "/dev", "-b", "/proc", "-b", "/sys",
            "-b", store.root.absolutePath + ":/work",
            *(if (File("/storage/emulated/0").list() != null) arrayOf("-b", "/storage/emulated/0:/sdcard") else emptyArray()),
            "-w", "/work", "/bin/sh", "-c", sh
        )
        val env = arrayOf(
            "PROOT_LOADER=" + File(nat(ctx), "libproot-loader.so").absolutePath,
            "PROOT_TMP_DIR=" + ctx.cacheDir.absolutePath,
            "PROOT_NO_SECCOMP=1",
            "LD_LIBRARY_PATH=" + libs.absolutePath + ":" + nat(ctx),
            "HOME=/root", "TERM=xterm-256color", "LANG=C.UTF-8",
            "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
        )
        return args to env
    }

    fun hasDotnet(ctx: Context) = File(root(ctx), "usr/bin/dotnet").exists() || File(root(ctx), "usr/lib/dotnet/dotnet").exists()

    fun hasGpp(ctx: Context) = File(root(ctx), "usr/bin/g++").exists()

    fun exec(ctx: Context, store: FileStore, cwd: File, cmd: String, onProc: (Process?) -> Unit = {}, say: (String) -> Unit) {
        if (!installed(ctx)) { say("Linux не установлен. Выполни: linux install"); return }
        LinuxService.begin(ctx)
        try { execInner(ctx, store, cwd, cmd, onProc, say) } finally { LinuxService.end(ctx) }
    }

    /** Helper scripts shipped in assets are copied into the Linux system before each run. */
    fun installScripts(ctx: Context) {
        try {
            val bin = File(root(ctx), "usr/local/bin").also { it.mkdirs() }
            // DNS must always work, otherwise pkg/pip/git fail (Developer: @yfeeeee)
            val rc = File(root(ctx), "etc/resolv.conf")
            if (!rc.exists() || rc.length() == 0L || !rc.readText().contains("nameserver")) {
                rc.parentFile?.mkdirs()
                rc.writeText("nameserver 1.1.1.1\nnameserver 8.8.8.8\n")
            }
            val t = File(bin, "yfeecs")
            ctx.assets.open("yfeecs.sh").use { i -> t.outputStream().use { o -> i.copyTo(o) } }
            t.setExecutable(true, false)
            val pkg = "#!/bin/sh\n# pkg/apt for YfeeCode (Alpine apk). Developer: @yfeeeee\nstamp=/var/lib/yfee-apk-stamp\nfresh() { [ -f \"\$stamp\" ] && [ -z \"\$(find \"\$stamp\" -mmin +1440 2>/dev/null)\" ]; }\nrefresh() { apk update && touch \"\$stamp\"; }\nc=\"\$1\"; [ \$# -gt 0 ] && shift\ncase \"\$c\" in\n install|i|add) fresh || refresh; apk add \"\$@\" || { refresh && apk add \"\$@\"; };;\n remove|uninstall|rm|del|purge) apk del \"\$@\";;\n update) refresh;;\n upgrade|full-upgrade|up) refresh && apk upgrade;;\n search) apk search \"\$@\";;\n list|list-installed) apk info -v | sort;;\n show|info) apk info -a \"\$@\";;\n files) apk info -L \"\$@\";;\n *) echo \"pkg install|remove|update|upgrade|search|list|show|files  (Alpine apk, индекс обновляется сам раз в сутки)\";;\nesac\n"
            for (n in listOf("pkg", "apt", "apt-get")) File(bin, n).also { it.writeText(pkg); it.setExecutable(true, false) }
            val scr = mapOf(
                "termux-clipboard-set" to "#!/bin/sh\n# Developer: @yfeeeee. Then press the Buffer button in the Linux tab.\ncat > /work/.yfee_clip\necho 'Скопировано. Нажми кнопку «Буфер» во вкладке Linux.' >&2\n",
                "termux-clipboard-get" to "#!/bin/sh\n# Press the Buffer button in the Linux tab first.\ncat /work/.yfee_clipin 2>/dev/null\n",
                "termux-open" to "#!/bin/sh\n[ -z \"\$1\" ] && { echo 'termux-open файл: копирует файл в /sdcard/Download'; exit 1; }\ncp \"\$1\" /sdcard/Download/ && echo \"Файл в /sdcard/Download/\$(basename \"\$1\")\"\n",
                "yfee-sshd" to "#!/bin/sh\n# SSH-сервер на порту 8022. Developer: @yfeeeee\ncommand -v sshd >/dev/null || apk add --no-cache openssh || exit 1\nssh-keygen -A\ngrep -q '^PermitRootLogin yes' /etc/ssh/sshd_config || echo 'PermitRootLogin yes' >> /etc/ssh/sshd_config\n[ -f /root/.pw_set ] || { echo 'Задай пароль для root:'; passwd root && touch /root/.pw_set; }\npkill sshd 2>/dev/null\n/usr/sbin/sshd -p 8022 -o UsePAM=no && echo 'sshd запущен. Подключение: ssh -p 8022 root@АДРЕС_ТЕЛЕФОНА'\nip addr 2>/dev/null | grep 'inet ' | grep -v 127.0.0.1\n",
                "yfee-backup" to "#!/bin/sh\n# Копия Linux в /sdcard/Download. Developer: @yfeeeee\n[ -d /sdcard/Download ] || { echo 'Нет доступа к памяти: кнопка Файлы'; exit 1; }\nf=/sdcard/Download/yfee-linux-backup.tgz\ncd / && tar czf \"\$f\" root etc usr var home opt lib bin sbin 2>/dev/null; echo \"Готово: \$f\"; ls -lh \"\$f\"\n",
                "yfee-restore" to "#!/bin/sh\n# Восстановление Linux из копии. Developer: @yfeeeee\nf=\"\${1:-/sdcard/Download/yfee-linux-backup.tgz}\"\n[ -f \"\$f\" ] || { echo \"Нет файла \$f\"; exit 1; }\ncd / && tar xzf \"\$f\" && echo 'Восстановлено. Начни новую сессию.'\n",
                "langs" to "#!/bin/sh\n# Установка языков одной командой. Developer: @yfeeeee\ncase \"\$1\" in\n node) apk add --no-cache nodejs npm;;\n go) apk add --no-cache go;;\n rust) apk add --no-cache rust cargo;;\n ruby) apk add --no-cache ruby;;\n php) apk add --no-cache php83 php83-cli && ln -sf /usr/bin/php83 /usr/local/bin/php;;\n java) apk add --no-cache openjdk17;;\n lua) apk add --no-cache lua5.4;;\n perl) apk add --no-cache perl;;\n *) echo 'langs node|go|rust|ruby|php|java|lua|perl  (поставить язык)';;\nesac\n",
                "termux-toast" to "#!/bin/sh\necho \"[toast] \$*\"\n",
                "yfee-help" to "#!/bin/sh\n# Шпаргалка YfeeCode. Developer: @yfeeeee\ncat <<'EOT'\nYfeeCode Linux (Alpine), разработчик @yfeeeee\n pkg install|remove|update|upgrade|search|list|show|files   пакеты (apt тоже работает)\n langs node|go|rust|ruby|php|java      установка языков\n yfee-git-setup                        настроить git (имя, почта, токен GitHub)\n yfee-bot имя / yfee-hikka имя          шаблоны Telegram-бота и модуля Heroku/Hikka\n yfee-venv [имя]                       виртуальное окружение Python\n yfee-keepalive команда                перезапуск команды при падении (для ботов)\n yfee-pg start|stop|status|psql        PostgreSQL (экспериментально)\n yfee-heroku install|login|init     деплой бота на Heroku (подсказки)\n yfee-doctor                          диагностика: сеть, DNS, пакеты, отчёт в /work/yfee-doctor.txt\n yfee-sshd                             SSH-сервер на порту 8022\n yfee-backup / yfee-restore            копия системы в /sdcard/Download\n termux-setup-storage                  доступ к памяти телефона (/sdcard)\n termux-clipboard-set / -get           буфер обмена (кнопка Буфер)\n /work                                 папка проектов приложения\n Кнопки: ⌨ скрыть клавиатуру, A- A+ шрифт, + сессия, закрыть, Файлы\nEOT\n",
                "yfee-git-setup" to "#!/bin/sh\n# Настройка git. Developer: @yfeeeee\ncommand -v git >/dev/null || apk add --no-cache git || exit 1\nprintf \"Имя для git: \"; read n\nprintf \"Почта для git: \"; read e\ngit config --global user.name \"\$n\"\ngit config --global user.email \"\$e\"\ngit config --global credential.helper store\ngit config --global init.defaultBranch main\necho \"Готово. При первом push на GitHub введи логин и токен (personal access token): он сохранится.\"\n",
                "yfee-hikka" to "#!/bin/sh\n# Шаблон модуля Heroku/Hikka. Developer: @yfeeeee\nn=\"\$1\"; [ -n \"\$n\" ] || { echo \"Использование: yfee-hikka имя\"; exit 1; }\nf=\"\$n.py\"; [ -e \"\$f\" ] && { echo \"Файл \$f уже есть\"; exit 1; }\ncat > \"\$f\" <<'EOT'\n# meta developer: @yfeeeee\nfrom .. import loader, utils\n\n\n@loader.tds\nclass MyMod(loader.Module):\n    \"\"\"Мой модуль\"\"\"\n\n    strings = {\"name\": \"MyMod\", \"hello\": \"Привет, {}!\"}\n\n    async def hellocmd(self, message):\n        \"\"\"<имя> - поздороваться\"\"\"\n        args = utils.get_args_raw(message) or \"мир\"\n        await utils.answer(message, self.strings(\"hello\").format(args))\nEOT\necho \"Создан \$f. Проверить: mod \$f .hello мир (вкладка Терминал)\"\n",
                "yfee-bot" to "#!/bin/sh\n# Шаблон Telegram-бота (aiogram 3). Developer: @yfeeeee\nn=\"\$1\"; [ -n \"\$n\" ] || { echo \"Использование: yfee-bot имя\"; exit 1; }\n[ -e \"\$n\" ] && { echo \"Папка \$n уже есть\"; exit 1; }\nmkdir -p \"\$n\" && cd \"\$n\" || exit 1\necho \"ВСТАВЬ_ТОКЕН_ОТ_BOTFATHER\" > token.txt\nprintf \"aiogram>=3.0\\n\" > requirements.txt\nprintf \"token.txt\\n__pycache__/\\n\" > .gitignore\ncat > bot.py <<'EOT'\n# YfeeCode, разработчик @yfeeeee\nimport asyncio\nimport pathlib\n\nfrom aiogram import Bot, Dispatcher, types\nfrom aiogram.filters import Command\n\nTOKEN = pathlib.Path(__file__).with_name(\"token.txt\").read_text().strip()\ndp = Dispatcher()\n\n\n@dp.message(Command(\"start\"))\nasync def start(m: types.Message):\n    await m.answer(\"Привет! Бот создан в YfeeCode (@yfeeeee)\")\n\n\n@dp.message()\nasync def echo(m: types.Message):\n    await m.answer(m.text or \"...\")\n\n\nasync def main():\n    await dp.start_polling(Bot(TOKEN))\n\n\nasyncio.run(main())\nEOT\necho \"Готово: папка \$n. Дальше: впиши токен в token.txt, pip install -r requirements.txt, python3 bot.py\"\n",
                "yfee-venv" to "#!/bin/sh\n# Виртуальное окружение Python. Developer: @yfeeeee\nn=\"\${1:-venv}\"\ncommand -v python3 >/dev/null || apk add --no-cache python3 py3-pip || exit 1\n[ -d \"\$n\" ] || python3 -m venv \"\$n\" || exit 1\necho \"Готово. Подключить: . \$n/bin/activate   (выйти: deactivate)\"\n",
                "yfee-keepalive" to "#!/bin/sh\n# Перезапуск команды при падении (для ботов). Developer: @yfeeeee\n[ \$# -gt 0 ] || { echo \"Использование: yfee-keepalive python3 bot.py\"; exit 1; }\nwhile true; do\n  \"\$@\"\n  echo \"[keepalive] команда завершилась с кодом \$?, перезапуск через 3 с (Ctrl+C: остановить)\"\n  sleep 3\ndone\n",
                "yfee-pg" to "#!/bin/sh\n# Помощник PostgreSQL. Developer: @yfeeeee\nd=/root/pgdata\ncase \"\$1\" in\n start)\n  command -v pg_ctl >/dev/null || apk add --no-cache postgresql postgresql-client || exit 1\n  mkdir -p /run/postgresql /var/log && chown postgres:postgres /run/postgresql 2>/dev/null\n  if [ ! -d \"\$d\" ]; then\n    mkdir -p \"\$d\" && chown postgres:postgres \"\$d\"\n    su postgres -c \"initdb -D \$d\" || exit 1\n  fi\n  su postgres -c \"pg_ctl -D \$d -l /var/log/pg.log start\" ;;\n stop) su postgres -c \"pg_ctl -D \$d stop\" ;;\n status) su postgres -c \"pg_ctl -D \$d status\" ;;\n psql) shift; su postgres -c \"psql \$*\" ;;\n *) echo \"Использование: yfee-pg start|stop|status|psql   (данные в \$d, лог /var/log/pg.log)\" ;;\nesac\n",
                "yfee-heroku" to "#!/bin/sh\n# Помощник деплоя на Heroku. Developer: @yfeeeee\ncase \"\$1\" in\n install)\n  command -v npm >/dev/null || apk add --no-cache nodejs npm || exit 1\n  npm install -g heroku || exit 1\n  echo \"Heroku CLI установлен. Дальше: yfee-heroku login\" ;;\n login) heroku login -i ;;\n init)\n  [ -f Procfile ] || echo \"worker: python3 bot.py\" > Procfile\n  [ -f requirements.txt ] || echo \"aiogram>=3.0\" > requirements.txt\n  [ -d .git ] || git init\n  echo \"Готово: Procfile и requirements.txt. Измени имя файла в Procfile, если бот не bot.py.\" ;;\n *)\n  echo \"Использование: yfee-heroku install|login|init\"\n  echo \"Порядок деплоя бота:\"\n  echo \" 1) yfee-heroku install   (один раз)\"\n  echo \" 2) yfee-heroku login\"\n  echo \" 3) в папке бота: yfee-heroku init, затем yfee-git-setup\"\n  echo \" 4) heroku create имя-приложения; git add -A; git commit -m deploy; git push heroku main\"\n  echo \" 5) heroku ps:scale worker=1; логи: heroku logs --tail\" ;;\nesac\n",
                "yfee-doctor" to "#!/bin/sh\n# Диагностика YfeeCode Linux. Developer: @yfeeeee\nout=/work/yfee-doctor.txt\n[ -d /work ] || out=/tmp/yfee-doctor.txt\n{\necho \"== YfeeCode doctor (@yfeeeee) ==\"\ndate\necho \"-- система\"; cat /etc/alpine-release 2>/dev/null; uname -a\necho \"-- место\"; df -h / /work 2>/dev/null\necho \"-- DNS\"; cat /etc/resolv.conf\necho \"-- сеть\"\nif wget -q -T 8 -O /dev/null http://dl-cdn.alpinelinux.org/alpine/ 2>/dev/null; then echo \"OK: репозиторий Alpine доступен\"; else echo \"ОШИБКА: нет доступа к dl-cdn.alpinelinux.org\"; fi\necho \"-- память телефона\"; ls /sdcard 2>/dev/null | head -5 || echo \"нет доступа к /sdcard\"\necho \"-- установлено\"\nfor c in bash git python3 pip node npm gcc sshd vim nano curl; do\n  if command -v \$c >/dev/null 2>&1; then echo \"есть: \$c\"; else echo \"нет:  \$c\"; fi\ndone\necho \"-- запись в /work\"\nif touch /work/.yfee_t 2>/dev/null; then echo \"OK\"; rm -f /work/.yfee_t; else echo \"ОШИБКА: нельзя писать в /work\"; fi\n} 2>&1 | tee \"\$out\"\necho\necho \"Отчёт сохранён: \$out (покажи его разработчику, если что-то не работает)\"\n"
            )
            for ((n, t) in scr) File(bin, n).also { it.writeText(t); it.setExecutable(true, false) }
            File(root(ctx), "root").mkdirs()
            File(root(ctx), "root/.profile").takeIf { !it.exists() }?.writeText(
                "if [ -n \"\$BASH_VERSION\" ]; then export PS1='\\[\\e[1;31m\\]yfee\\[\\e[0m\\]:\\w\\$ '; else export PS1='yfee# '; fi\nalias ll='ls -la'\nalias ..='cd ..'\ncd /work 2>/dev/null\n" +
                "echo 'YfeeCode Linux (Alpine). Разработчик: @yfeeeee'\necho 'pkg install <пакет>  |  termux-setup-storage  |  yfee-sshd  |  /sdcard  |  /work (проекты)'\n"
            )
            // always rewritten: shell extras for every login shell (Developer: @yfeeeee)
            File(root(ctx), "etc/profile.d").mkdirs()
            File(root(ctx), "etc/profile.d/yfee-extra.sh").writeText(
                "# YfeeCode extras. Developer: @yfeeeee\nalias ll='ls -lah --color=auto'\nalias la='ls -A --color=auto'\nalias ls='ls --color=auto'\nalias grep='grep --color=auto'\nalias gs='git status'\n" +
                "if [ -n \"\$BASH_VERSION\" ]; then\n  export HISTFILE=/root/.bash_history HISTSIZE=5000 HISTFILESIZE=10000\n  shopt -s histappend 2>/dev/null\n  PROMPT_COMMAND='printf \"\\033]0;%s\\007\" \"\${PWD##*/}\"'\n  [ -f /usr/share/bash-completion/bash_completion ] && . /usr/share/bash-completion/bash_completion\nfi\n"
            )
            File(bin, "termux-setup-storage").also {
                it.writeText("#!/bin/sh\nif [ -d /sdcard/Download ]; then echo 'Память телефона доступна: /sdcard (Download, Documents, DCIM)'; else echo 'Нет доступа. В приложении: вкладка Linux, кнопка Файлы, разреши доступ ко всем файлам, затем новая сессия.'; fi\n")
                it.setExecutable(true, false)
            }
        } catch (_: Exception) {}
    }

    private fun execInner(ctx: Context, store: FileStore, cwd: File, cmd: String, onProc: (Process?) -> Unit, say: (String) -> Unit) {
        installScripts(ctx)
        val libs = prepareLibs(ctx)
        val rel = cwd.relativeToOrSelf(store.root).path
        val work = if (rel.isEmpty() || rel == ".") "/work" else "/work/$rel"
        val args = listOf(
            File(nat(ctx), "libproot.so").absolutePath, "-0", "--link2symlink",
            "-r", root(ctx).absolutePath,
            "-b", "/dev", "-b", "/proc", "-b", "/sys",
            "-b", store.root.absolutePath + ":/work",
            "-w", work, "/bin/sh", "-c", cmd
        )
        val pb = ProcessBuilder(args).redirectErrorStream(true)
        val env = pb.environment()
        env["PROOT_LOADER"] = File(nat(ctx), "libproot-loader.so").absolutePath
        env["PROOT_TMP_DIR"] = ctx.cacheDir.absolutePath
        env["PROOT_NO_SECCOMP"] = "1"
        env["LD_LIBRARY_PATH"] = libs.absolutePath + ":" + nat(ctx)
        env["HOME"] = "/root"
        env["TERM"] = "dumb"
        env["PATH"] = "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
        val p = pb.start()
        onProc(p)
        // read raw bytes so prompts without a newline (cout << "x: ") are shown too
        val ins = p.inputStream
        val buf = StringBuilder()
        val bytes = java.io.ByteArrayOutputStream()
        var idle = 0
        fun flushText() {
            if (bytes.size() > 0) { buf.append(String(bytes.toByteArray(), Charsets.UTF_8)); bytes.reset() }
        }
        while (true) {
            if (ins.available() > 0) {
                val b = ins.read()
                if (b < 0) break
                idle = 0
                if (b == '\n'.code) { flushText(); say(buf.toString().trimEnd('\r')); buf.setLength(0) } else bytes.write(b)
            } else {
                if (!p.isAlive && ins.available() == 0) break
                Thread.sleep(20)
                idle++
                if (idle == 10) { flushText(); if (buf.isNotEmpty()) { say(buf.toString()); buf.setLength(0) } }
            }
        }
        flushText()
        if (buf.isNotEmpty()) say(buf.toString())
        p.waitFor()
        onProc(null)
    }
}
