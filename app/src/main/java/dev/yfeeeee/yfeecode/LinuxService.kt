// YfeeCode — developer: @yfeeeee
package dev.yfeeeee.yfeecode

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.PowerManager
import android.os.Process

/**
 * Keeps the app process alive while Linux programs (PostgreSQL, bots, shells) are running:
 * foreground notification + CPU and Wi-Fi locks.
 */
class LinuxService : Service() {
    private var wake: PowerManager.WakeLock? = null
    private var wifi: WifiManager.WifiLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            Process.killProcess(Process.myPid()) // also ends every Linux child process
            return START_NOT_STICKY
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel("linux", "Linux", NotificationManager.IMPORTANCE_LOW))
        val fl = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), fl)
        val stop = PendingIntent.getService(this, 2, Intent(this, LinuxService::class.java).setAction(ACTION_STOP), fl)
        val n = Notification.Builder(this, "linux")
            .setContentTitle("YfeeCode: Linux работает")
            .setContentText("Программы в фоне не закроются. Нажми «Остановить», чтобы завершить всё.")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(open)
            .addAction(Notification.Action.Builder(null, "Остановить", stop).build())
            .build()
        startForeground(2, n)
        if (wake == null) try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "yfeecode:linux").apply { setReferenceCounted(false); acquire() }
            @Suppress("DEPRECATION")
            val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            wifi = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "yfeecode:linux").apply { setReferenceCounted(false); acquire() }
        } catch (_: Throwable) {}
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        try { wake?.release(); wifi?.release() } catch (_: Throwable) {}
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "stop"
        private var users = 0

        /** Call when a Linux program/session starts; the service runs while at least one is active. */
        @Synchronized fun begin(ctx: Context) {
            if (users++ == 0) {
                try { androidx.core.content.ContextCompat.startForegroundService(ctx, Intent(ctx, LinuxService::class.java)) } catch (_: Throwable) {}
                android.os.Handler(android.os.Looper.getMainLooper()).post { Battery.requestIfNeeded(ctx) }
            }
        }

        @Synchronized fun end(ctx: Context) {
            if (users > 0 && --users == 0) ctx.stopService(Intent(ctx, LinuxService::class.java))
        }
    }
}
