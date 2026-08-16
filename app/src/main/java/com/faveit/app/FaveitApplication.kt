package com.faveit.app

import android.app.Application
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
