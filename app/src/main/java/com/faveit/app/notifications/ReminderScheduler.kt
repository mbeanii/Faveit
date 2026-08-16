package com.faveit.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val UNIQUE_WORK = "faveit_gentle_reminder"

    fun schedule(context: Context) {
        enqueue(context, ExistingPeriodicWorkPolicy.UPDATE)
    }

    fun reconcile(context: Context, enabled: Boolean) {
        if (enabled) enqueue(context, ExistingPeriodicWorkPolicy.KEEP) else cancel(context)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
    }

    private fun enqueue(context: Context, policy: ExistingPeriodicWorkPolicy) {
        val request = PeriodicWorkRequestBuilder<FavoriteReminderWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(3, TimeUnit.DAYS)
            .addTag(UNIQUE_WORK)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(UNIQUE_WORK, policy, request)
    }
}
