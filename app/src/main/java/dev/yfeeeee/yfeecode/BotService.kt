// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.os.Process
import com.chaquo.python.Python

/**
 * Runs a Python script in a separate process (:bot).
 * Stopping = killing that process, which reliably stops asyncio bots.
 */
class BotService : Service() {

    private var wake: PowerManager.WakeLock? = null
    private var wifi: WifiManager.WifiLock? = null
    private var started = false

    override fun onBind(intent: Intent?): IBinder? = null

    private fun state() = getSharedPreferences("bot_state", Context.MODE_PRIVATE)

    /** Keeps the CPU and Wi-Fi awake while the bot runs, so it does not freeze when the screen is off. */
    private fun acquireLocks() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "yfeecode:bot").apply { setReferenceCounted(false); acquire() }
            @Suppress("DEPRECATION")
            val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            wifi = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "yfeecode:bot").apply { setReferenceCounted(false); acquire() }
        } catch (_: Throwable) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            state().edit().putBoolean("running", false).apply()
            stopForeground(STOP_FOREGROUND_REMOVE)
            Process.killProcess(Process.myPid())
            return START_NOT_STICKY
        }
        // after a system kill Android restarts the service with a null intent: resume the saved script
        val script = intent?.getStringExtra(EXTRA_SCRIPT)
            ?: state().takeIf { it.getBoolean("running", false) }?.getString("script", null)
            ?: return START_NOT_STICKY
        val log = intent?.getStringExtra(EXTRA_LOG)
            ?: state().getString("log", null)
            ?: return START_NOT_STICKY
        if (started) return START_STICKY
        started = true
        state().edit().putBoolean("running", true).putString("script", script).putString("log", log).apply()
        startForeground(1, buildNotification(script))
        acquireLocks()
        // show the last line of the bot log in the notification
        Thread {
            var shown = ""
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            while (true) {
                try {
                    Thread.sleep(5000)
                    val f = java.io.File(log)
                    val line = if (f.isFile) f.useLines { it.lastOrNull { l -> l.isNotBlank() } }.orEmpty().take(120) else ""
                    if (line != shown) { shown = line; nm.notify(1, buildNotification(script, line)) }
                } catch (_: InterruptedException) { return@Thread } catch (_: Throwable) {}
            }
        }.apply { isDaemon = true }.start()
        if (intent == null) java.io.File(log).appendText("[run] Бот перезапущен после остановки системой\n")
        Thread {
            try {
                Python.getInstance().getModule("runner").callAttr("run", script, log)
            } catch (e: Throwable) {
                java.io.File(log).appendText("[error] " + e.message + "\n")
            }
            // script finished by itself
            state().edit().putBoolean("running", false).apply()
            stopForeground(STOP_FOREGROUND_REMOVE)
            Process.killProcess(Process.myPid())
        }.start()
        return START_STICKY
    }

    override fun onDestroy() {
        try { wake?.release(); wifi?.release() } catch (_: Throwable) {}
        super.onDestroy()
    }

    private fun buildNotification(script: String, lastLine: String = ""): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel("bot", "Bot runner", NotificationManager.IMPORTANCE_LOW)
        )
        val flags = android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        val open = android.app.PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), flags)
        val stop = android.app.PendingIntent.getService(this, 1, Intent(this, BotService::class.java).setAction(ACTION_STOP), flags)
        return Notification.Builder(this, "bot")
            .setContentTitle("YfeeCode: бот работает")
            .setContentText(if (lastLine.isBlank()) script.substringAfterLast('/') else lastLine)
            .setSubText(script.substringAfterLast('/'))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(open)
            .addAction(Notification.Action.Builder(null, "Остановить", stop).build())
            .build()
    }

    companion object {
        const val ACTION_STOP = "stop"
        const val EXTRA_SCRIPT = "script"
        const val EXTRA_LOG = "log"

        fun start(ctx: Context, script: String, log: String) {
            val i = Intent(ctx, BotService::class.java)
                .putExtra(EXTRA_SCRIPT, script).putExtra(EXTRA_LOG, log)
            ctx.startForegroundService(i)
        }

        fun stop(ctx: Context) {
            ctx.startService(Intent(ctx, BotService::class.java).setAction(ACTION_STOP))
        }
    }
}

/** Asks Android once to not put the app (and the bot) to sleep in the background. */
object Battery {
    private var asked = false

    fun isIgnoring(ctx: Context): Boolean =
        (ctx.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(ctx.packageName)

    /** Returns true if the system dialog was opened. */
    fun requestIfNeeded(ctx: Context, force: Boolean = false): Boolean {
        if ((asked && !force) || isIgnoring(ctx)) return false
        asked = true
        return try {
            ctx.startActivity(
                Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    android.net.Uri.parse("package:${ctx.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            true
        } catch (_: Throwable) { false }
    }
}
