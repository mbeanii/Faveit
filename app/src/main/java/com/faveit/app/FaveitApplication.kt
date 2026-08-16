package com.faveit.app

import android.app.Application
import com.faveit.app.data.FaveitRepository
import com.faveit.app.notifications.FavoriteReminderWorker

class FaveitApplication : Application() {
    lateinit var repository: FaveitRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = FaveitRepository(this)
        FavoriteReminderWorker.createNotificationChannel(this)
    }
}
