// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings as AndroidSettings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/** Checks the GitHub release "latest" and installs a newer APK over the current one. */
object Updater {
    private const val API = "https://api.github.com/repos/den1511bol/YfeeCode/releases/tags/latest"

    var status by mutableStateOf("")          // text shown in settings
    var available by mutableStateOf(false)    // a newer build exists
    var busy by mutableStateOf(false)
    var progress by mutableStateOf(-1)        // 0..100 while downloading, -1 otherwise
    private var apkUrl = ""
    private var remoteSha = ""
    private var autoChecked = false
    var ready by mutableStateOf(false)        // the new APK is already downloaded and verified

    val currentSha: String get() = BuildConfig.GIT_SHA
    val currentLabel: String get() = "v${BuildConfig.VERSION_NAME} (${currentSha.take(7)})"

    private fun open(url: String): HttpURLConnection =
        (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000; readTimeout = 30000
            setRequestProperty("User-Agent", "YfeeCode-updater")
            setRequestProperty("Accept", "application/vnd.github+json")
            instanceFollowRedirects = true
        }

    /** Auto check once per app start. */
    fun autoCheck(ctx: Context) {
        if (autoChecked || !Settings.autoUpdate) return
        autoChecked = true
        check(ctx.applicationContext, silent = true)
    }

    fun check(ctx: Context, silent: Boolean = false) {
        if (busy) return
        busy = true
        if (!silent) status = "Проверяю…"
        thread {
            try {
                val c = open(API)
                if (c.responseCode != 200) throw RuntimeException("GitHub ответил ${c.responseCode}")
                val json = JSONObject(c.inputStream.bufferedReader().readText())
                val body = json.optString("body")
                remoteSha = Regex("""Commit:\s*([0-9a-f]{7,40})""").find(body)?.groupValues?.get(1).orEmpty()
                val assets = json.optJSONArray("assets")
                apkUrl = if (assets != null && assets.length() > 0) assets.getJSONObject(0).optString("browser_download_url") else ""
                val newer = remoteSha.isNotEmpty() && apkUrl.isNotEmpty() && currentSha != "dev" && remoteSha != currentSha
                available = newer
                if (newer) ready = cachedApk(ctx)?.let { verify(ctx, it) } == true
                status = when {
                    newer && ready -> "Новая версия скачана, осталось установить"
                    newer -> "Доступна новая версия (${remoteSha.take(7)})"
                    currentSha == "dev" -> "Это локальная сборка, обновления отключены"
                    else -> "У тебя последняя версия"
                }
            } catch (e: Exception) {
                if (!silent) status = "Нет связи с GitHub: ${e.message}"
            } finally {
                busy = false
            }
            // download in the background right away, so installing later is a single tap
            if (available && !ready && silent) fetchApk(ctx, installAfter = false)
        }
    }

    private fun cachedApk(ctx: Context): File? =
        File(File(ctx.cacheDir, "updates"), "YfeeCode-$remoteSha.apk").takeIf { it.isFile }

    /** The file must be our own app and newer than the installed one, otherwise Android would refuse it. */
    private fun verify(ctx: Context, f: File): Boolean = try {
        val info = ctx.packageManager.getPackageArchiveInfo(f.absolutePath, 0)
        @Suppress("DEPRECATION")
        info != null && info.packageName == ctx.packageName && info.versionCode > BuildConfig.VERSION_CODE
    } catch (e: Exception) { false }

    /** Installs the downloaded update (downloads it first if needed). */
    fun downloadAndInstall(ctx: Context) {
        val app = ctx.applicationContext
        val f = cachedApk(app)
        if (f != null && verify(app, f)) { install(ctx, f); return }
        fetchApk(ctx, installAfter = true)
    }

    private fun fetchApk(ctx: Context, installAfter: Boolean) {
        if (busy || apkUrl.isEmpty()) return
        busy = true; progress = 0; status = "Скачиваю обновление…"
        val app = ctx.applicationContext
        thread {
            try {
                val dir = File(app.cacheDir, "updates").apply { mkdirs() }
                dir.listFiles()?.forEach { it.delete() }
                val tmp = File(dir, "download.tmp")
                val out = File(dir, "YfeeCode-$remoteSha.apk")
                val c = open(apkUrl)
                if (c.responseCode != 200) throw RuntimeException("скачивание: ${c.responseCode}")
                val total = c.contentLengthLong
                c.inputStream.use { input ->
                    tmp.outputStream().use { o ->
                        val buf = ByteArray(64 * 1024)
                        var done = 0L
                        while (true) {
                            val n = input.read(buf)
                            if (n < 0) break
                            o.write(buf, 0, n)
                            done += n
                            if (total > 0) progress = (done * 100 / total).toInt()
                        }
                    }
                }
                if (total > 0 && tmp.length() != total) throw RuntimeException("файл скачан не полностью")
                tmp.renameTo(out)
                if (!verify(app, out)) { out.delete(); throw RuntimeException("файл не подходит для установки") }
                ready = true
                status = "Новая версия скачана, осталось установить"
                if (installAfter) install(app, out)
            } catch (e: Exception) {
                status = "Ошибка обновления: ${e.message}"
            } finally {
                busy = false; progress = -1
            }
        }
    }

    private fun install(ctx: Context, apk: File) {
        if (!ctx.packageManager.canRequestPackageInstalls()) {
            status = "Разреши установку из этого приложения и нажми «Обновить» ещё раз"
            ctx.startActivity(
                Intent(AndroidSettings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${ctx.packageName}"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return
        }
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", apk)
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
