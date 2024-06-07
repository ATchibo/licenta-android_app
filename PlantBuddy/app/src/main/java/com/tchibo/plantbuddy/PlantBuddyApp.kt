package com.tchibo.plantbuddy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PlantBuddyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val name = "Notification Channel"
        val description ="-"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel("NotificationChannel", name, importance)
        channel.description = description;

        val notificationManager : NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}