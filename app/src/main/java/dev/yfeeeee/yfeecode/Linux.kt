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

    private fun prepareLibs(ctx: Context): File {
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
    fun hasGpp(ctx: Context) = File(root(ctx), "usr/bin/g++").exists()

    fun exec(ctx: Context, store: FileStore, cwd: File, cmd: String, onProc: (Process?) -> Unit = {}, say: (String) -> Unit) {
        if (!installed(ctx)) { say("Linux не установлен. Выполни: linux install"); return }
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
