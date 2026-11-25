// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.chaquo.python.Python

/**
 * Runs a Python script in a separate process (:bot).
 * Stopping = killing that process, which reliably stops asyncio bots.
 */
class BotService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            Process.killProcess(Process.myPid())
            return START_NOT_STICKY
        }
        val script = intent?.getStringExtra(EXTRA_SCRIPT) ?: return START_NOT_STICKY
        val log = intent.getStringExtra(EXTRA_LOG) ?: return START_NOT_STICKY
        startForeground(1, buildNotification(script))
        Thread {
            try {
                Python.getInstance().getModule("runner").callAttr("run", script, log)
            } catch (e: Throwable) {
                java.io.File(log).appendText("[error] " + e.message + "\n")
            }
            // script finished by itself
            stopForeground(STOP_FOREGROUND_REMOVE)
            Process.killProcess(Process.myPid())
        }.start()
        return START_NOT_STICKY
    }

    private fun buildNotification(script: String): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel("bot", "Bot runner", NotificationManager.IMPORTANCE_LOW)
        )
        return Notification.Builder(this, "bot")
            .setContentTitle("YfeeCode: running")
            .setContentText(script.substringAfterLast('/'))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
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
