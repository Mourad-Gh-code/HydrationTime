package com.example.hydrationtime.notifications

import android.content.Context
import androidx.work.*
import com.example.hydrationtime.ui.adapters.NotificationTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_TAG_PREFIX = "hydration_reminder_"

    fun scheduleNotifications(context: Context, times: List<NotificationTime>) {
        // Cancel all existing notification work
        WorkManager.getInstance(context).cancelAllWorkByTag("hydration_reminder")

        times.forEach { time ->
            if (time.isActive) {
                scheduleNotificationForTime(context, time)
            }
        }

        // Save scheduled state
        val sharedPrefs = context.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("reminders_scheduled", times.any { it.isActive }).apply()
    }

    private fun scheduleNotificationForTime(context: Context, time: NotificationTime) {
        val currentTime = Calendar.getInstance()
        val notificationTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has passed for today, schedule for tomorrow
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = notificationTime.timeInMillis - currentTime.timeInMillis

        // Create daily repeating work request
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, // Repeat every 24 hours
            TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .addTag("hydration_reminder")
            .build()

        // Enqueue work with unique name based on time
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "$WORK_TAG_PREFIX${time.hour}_${time.minute}",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("hydration_reminder")

        val sharedPrefs = context.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("reminders_scheduled", false).apply()
    }

    fun areRemindersScheduled(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("reminders_scheduled", false)
    }
}
