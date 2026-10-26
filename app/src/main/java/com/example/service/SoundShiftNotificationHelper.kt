package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.VolumeMode
import com.example.receiver.VolumeActionReceiver

object SoundShiftNotificationHelper {

    const val CHANNEL_ID = "quadmode_control_channel"
    const val NOTIFICATION_ID = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Керування QuadMode"
            val descriptionText = "Швидке перемикання 4 режимів з іконками у шторці"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(context: Context, activeMode: VolumeMode, details: String = "") {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        fun createModePendingIntent(action: String, requestCode: Int): PendingIntent {
            val intent = Intent(context, VolumeActionReceiver::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val outdoorPendingIntent = createModePendingIntent(VolumeActionReceiver.ACTION_SET_OUTDOOR, 101)
        val dayPendingIntent = createModePendingIntent(VolumeActionReceiver.ACTION_SET_DAY, 102)
        val silentPendingIntent = createModePendingIntent(VolumeActionReceiver.ACTION_SET_SILENT, 103)
        val nightPendingIntent = createModePendingIntent(VolumeActionReceiver.ACTION_SET_NIGHT, 104)

        val modeTitle = when (activeMode) {
            VolumeMode.OUTDOOR -> "🟠 Вуличний"
            VolumeMode.DAY -> "⚪ Денний"
            VolumeMode.SILENT -> "🟢 Беззвучний"
            VolumeMode.NIGHT -> "🔵 Нічний"
        }

        val contentSubtitle = if (details.isNotEmpty()) details else "Оберіть потрібний режим"

        // Build Custom RemoteViews with 4 Prominent Icon Buttons
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_quadmode).apply {
            setTextViewText(R.id.notif_title, "QuadMode: $modeTitle")
            setTextViewText(R.id.notif_subtitle, contentSubtitle)

            // Button 0: Outdoor
            setInt(
                R.id.btn_notif_outdoor,
                "setBackgroundResource",
                if (activeMode == VolumeMode.OUTDOOR) R.drawable.bg_notif_btn_active_outdoor else R.drawable.bg_notif_btn_normal
            )
            setOnClickPendingIntent(R.id.btn_notif_outdoor, outdoorPendingIntent)

            // Button 1: Day
            setInt(
                R.id.btn_notif_day,
                "setBackgroundResource",
                if (activeMode == VolumeMode.DAY) R.drawable.bg_notif_btn_active_day else R.drawable.bg_notif_btn_normal
            )
            setOnClickPendingIntent(R.id.btn_notif_day, dayPendingIntent)

            // Button 2: Silent
            setInt(
                R.id.btn_notif_silent,
                "setBackgroundResource",
                if (activeMode == VolumeMode.SILENT) R.drawable.bg_notif_btn_active_silent else R.drawable.bg_notif_btn_normal
            )
            setOnClickPendingIntent(R.id.btn_notif_silent, silentPendingIntent)

            // Button 3: Night
            setInt(
                R.id.btn_notif_night,
                "setBackgroundResource",
                if (activeMode == VolumeMode.NIGHT) R.drawable.bg_notif_btn_active_night else R.drawable.bg_notif_btn_normal
            )
            setOnClickPendingIntent(R.id.btn_notif_night, nightPendingIntent)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openAppPendingIntent)

        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS permission might not be granted
        }
    }

    fun cancelNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
