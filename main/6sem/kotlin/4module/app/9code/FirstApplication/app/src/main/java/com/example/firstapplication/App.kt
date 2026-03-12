package com.example.firstapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class App : Application() {
    companion object {
        const val CHANNEL_ID = "weather_channel"
        const val NOTIFICATION_ID = 42
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Прогноз погоды",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Уведомления о ходе сбора прогноза"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}