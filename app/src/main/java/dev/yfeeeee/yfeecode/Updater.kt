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
    fun autoCheck() {
        if (autoChecked || !Settings.autoUpdate) return
        autoChecked = true
        check(silent = true)
    }

    fun check(silent: Boolean = false) {
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
                status = when {
                    newer -> "Доступна новая версия (${remoteSha.take(7)})"
                    currentSha == "dev" -> "Это локальная сборка, обновления отключены"
                    else -> "У тебя последняя версия"
                }
            } catch (e: Exception) {
                if (!silent) status = "Нет связи с GitHub: ${e.message}"
            } finally {
                busy = false
            }
        }
    }

    /** Downloads the APK into the cache and opens the system installer. */
    fun downloadAndInstall(ctx: Context) {
        if (busy || apkUrl.isEmpty()) return
        busy = true; progress = 0; status = "Скачиваю…"
        thread {
            try {
                val dir = File(ctx.cacheDir, "updates").apply { mkdirs() }
                dir.listFiles()?.forEach { it.delete() }
                val out = File(dir, "YfeeCode.apk")
                val c = open(apkUrl)
                if (c.responseCode != 200) throw RuntimeException("скачивание: ${c.responseCode}")
                val total = c.contentLengthLong
                c.inputStream.use { input ->
                    out.outputStream().use { o ->
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
                if (out.length() < 1_000_000) throw RuntimeException("файл слишком маленький")
                status = "Готово, открываю установщик"
                install(ctx, out)
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
