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
        //Let's call the function.
        createNotificationChannel()
    }

    //Create Notification Channel.
    private fun createNotificationChannel(){
        val name = "JetpackPushNotification"
        val description ="Jetpack Push Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH

        //Now Create Notification Channel.
        // it take three parameters. notification id,name, and importance.
        val channel = NotificationChannel("Login",name,importance)
        channel.description = description;

        // Get Notification Manager.
        val notificationManager : NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Lets Create Notification channel.
        notificationManager.createNotificationChannel(channel)

    }
}