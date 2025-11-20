package com.example.hydrationtime.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.hydrationtime.ui.main.MainActivity
import com.example.hydrationtime.R

class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPrefs = context.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)

        if (!notificationsEnabled) {
            return Result.success()
        }

        // Get active messages from saved list
        val messagesJson = sharedPrefs.getString("notification_messages", null)
        val message = if (messagesJson != null) {
            try {
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.hydrationtime.ui.adapters.NotificationMessage>>() {}.type
                val messages: List<com.example.hydrationtime.ui.adapters.NotificationMessage> = gson.fromJson(messagesJson, type)
                val activeMessages = messages.filter { it.isActive }
                if (activeMessages.isNotEmpty()) {
                    activeMessages.random().message
                } else {
                    "Time to drink water!"
                }
            } catch (e: Exception) {
                "Time to drink water!"
            }
        } else {
            // Default messages
            val defaultMessages = listOf(
                "Ana, time to drink more water!",
                "Time for another glass of water!",
                "Ana, it's water time!",
                "Have some water to stay hydrated!",
                "Ana, time for a break with a cup of water!"
            )
            defaultMessages.random()
        }

        // Get notification sound
        val soundUriString = sharedPrefs.getString("notification_sound", null)
        val soundUri = if (soundUriString != null) {
            Uri.parse(soundUriString)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        sendNotification(message, soundUri)

        return Result.success()
    }

    private fun sendNotification(message: String, soundUri: Uri) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water"
                enableVibration(true)
                setSound(soundUri, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("Hydration Time")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "hydration_reminder_channel"
        const val NOTIFICATION_ID = 1001
    }
}
