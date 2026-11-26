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
            val pkg = "#!/bin/sh\n# pkg/apt for YfeeCode (Alpine apk). Developer: @yfeeeee\nc=\"\$1\"; [ \$# -gt 0 ] && shift\ncase \"\$c\" in\n install|i|add) apk add \"\$@\";;\n remove|uninstall|rm|del|purge) apk del \"\$@\";;\n update) apk update;;\n upgrade|full-upgrade|up) apk update && apk upgrade;;\n search) apk search \"\$@\";;\n list|list-installed) apk info;;\n show|info) apk info -a \"\$@\";;\n *) echo \"pkg install|remove|update|upgrade|search|list  (Alpine apk)\";;\nesac\n"
            for (n in listOf("pkg", "apt", "apt-get")) File(bin, n).also { it.writeText(pkg); it.setExecutable(true, false) }
            val scr = mapOf(
                "termux-clipboard-set" to "#!/bin/sh\n# Developer: @yfeeeee. Then press the Buffer button in the Linux tab.\ncat > /work/.yfee_clip\necho 'Скопировано. Нажми кнопку «Буфер» во вкладке Linux.' >&2\n",
                "termux-clipboard-get" to "#!/bin/sh\n# Press the Buffer button in the Linux tab first.\ncat /work/.yfee_clipin 2>/dev/null\n",
                "termux-open" to "#!/bin/sh\n[ -z \"\$1\" ] && { echo 'termux-open файл: копирует файл в /sdcard/Download'; exit 1; }\ncp \"\$1\" /sdcard/Download/ && echo \"Файл в /sdcard/Download/\$(basename \"\$1\")\"\n",
                "yfee-sshd" to "#!/bin/sh\n# SSH-сервер на порту 8022. Developer: @yfeeeee\ncommand -v sshd >/dev/null || apk add --no-cache openssh || exit 1\nssh-keygen -A\ngrep -q '^PermitRootLogin yes' /etc/ssh/sshd_config || echo 'PermitRootLogin yes' >> /etc/ssh/sshd_config\n[ -f /root/.pw_set ] || { echo 'Задай пароль для root:'; passwd root && touch /root/.pw_set; }\npkill sshd 2>/dev/null\n/usr/sbin/sshd -p 8022 -o UsePAM=no && echo 'sshd запущен. Подключение: ssh -p 8022 root@АДРЕС_ТЕЛЕФОНА'\nip addr 2>/dev/null | grep 'inet ' | grep -v 127.0.0.1\n",
                "termux-toast" to "#!/bin/sh\necho \"[toast] \$*\"\n"
            )
            for ((n, t) in scr) File(bin, n).also { it.writeText(t); it.setExecutable(true, false) }
            File(root(ctx), "root").mkdirs()
            File(root(ctx), "root/.profile").takeIf { !it.exists() }?.writeText(
                "export PS1='\\[\\e[1;31m\\]yfee\\[\\e[0m\\]:\\w\\$ '\nalias ll='ls -la'\nalias ..='cd ..'\ncd /work 2>/dev/null\n" +
                "echo 'YfeeCode Linux (Alpine). Разработчик: @yfeeeee'\necho 'pkg install <пакет>  |  termux-setup-storage  |  yfee-sshd  |  /sdcard  |  /work (проекты)'\n"
            )
            File(bin, "termux-setup-storage").also {
                it.writeText("#!/bin/sh\nif [ -d /sdcard/Download ]; then echo 'Память телефона доступна: /sdcard (Download, Documents, DCIM)'; else echo 'Нет доступа. В приложении: вкладка Linux, кнопка Файлы, разреши доступ ко всем файлам, затем новая сессия.'; fi\n")
                it.setExecutable(true, false)
            }
            val t = File(bin, "yfeecs")
            ctx.assets.open("yfeecs.sh").use { i -> t.outputStream().use { o -> i.copyTo(o) } }
            t.setExecutable(true, false)
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
