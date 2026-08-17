package com.faveit.app

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.faveit.app.data.FaveitRepository
import com.faveit.app.notifications.FavoriteReminderWorker
import com.faveit.app.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FaveitApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var repository: FaveitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        // The Startup initializer is deliberately removed in the manifest so
        // this is the sole, deterministic initialization point for reminders.
        WorkManager.initialize(this, Configuration.Builder().build())

        repository = FaveitRepository(this)
        FavoriteReminderWorker.createNotificationChannel(this)
        applicationScope.launch {
            ReminderScheduler.reconcile(
                context = this@FaveitApplication,
                enabled = repository.currentPreferences().remindersEnabled,
            )
        }
    }
}
