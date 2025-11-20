package com.example.hydrationtime.workers

import android.content.Context
import androidx.work.*
import com.example.hydrationtime.utils.NotificationUtils
import java.util.concurrent.TimeUnit

/**
 * WaterReminderWorker - Worker pour les rappels d'hydratation
 */
class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        // Afficher la notification
        NotificationUtils.showWaterReminderNotification(applicationContext)

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "WaterReminderWork"

        /**
         * Planifier les rappels d'eau (toutes les 2 heures par exemple)
         */
        fun scheduleReminder(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val repeatingRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                2, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }

        /**
         * Annuler les rappels
         */
        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}