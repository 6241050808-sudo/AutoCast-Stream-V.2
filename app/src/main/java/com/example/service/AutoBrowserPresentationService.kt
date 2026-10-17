package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.presentation.CarDisplayManager

/**
 * Foreground Service that maintains the active Car Screen Presentation and Video Stream
 * even when the mobile phone screen is locked or turned off.
 */
class AutoBrowserPresentationService : Service() {

    private var carDisplayManager: CarDisplayManager? = null

    companion object {
        private const val CHANNEL_ID = "autobrowser_car_channel"
        private const val NOTIFICATION_ID = 204

        fun startService(context: Context) {
            val intent = Intent(context, AutoBrowserPresentationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AutoBrowserPresentationService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("AutoBrowser กำลังแสดงผลบนหน้าจอ Android Auto"))

        carDisplayManager = CarDisplayManager(applicationContext).apply {
            startListening()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "AutoBrowser Car Stream Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "รักษาการเชื่อมต่อและการเล่นวิดีโอบนหน้าจอ Android Auto"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AutoBrowser for Android Auto")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        carDisplayManager?.stopListening()
        carDisplayManager = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
