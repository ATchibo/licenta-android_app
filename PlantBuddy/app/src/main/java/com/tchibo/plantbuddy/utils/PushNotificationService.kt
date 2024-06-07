package com.tchibo.plantbuddy.utils

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tchibo.plantbuddy.MainActivity
import kotlinx.serialization.json.Json

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Title"
        val message = remoteMessage.notification?.body ?: "Message"
        val data = if (remoteMessage.data.isNotEmpty() && remoteMessage.data["data"] != null) {
            remoteMessage.data["data"]
        } else {
            null
        }

        if (data != null) {
            val dataMap: Map<String, String> = Json.decodeFromString(data)

            when (dataMap["type"]) {
                "LOG" -> {
                    launchIntent(
                        title,
                        message,
                        data,
                        MainActivity::class.java,
                        android.R.drawable.ic_dialog_info
                    )
                }
                "LOGIN_REQUEST" -> {
                    launchIntent(
                        title,
                        message,
                        data,
                        MainActivity::class.java,
                        android.R.drawable.ic_dialog_alert
                    )
                }
            }
        }
    }

    private fun launchIntent (
        title: String,
        message: String,
        extraData: String?,
        targetClass: Class<*>,
        notificationIcon: Int
    ) {
        val intent = Intent(this, targetClass).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        if (extraData != null) {
            intent.putExtra("data", extraData)
        }

        intent.putExtra("title",title)
        intent.putExtra("body", message)

        val requestCode = System.currentTimeMillis().toInt()
        val pendingIntent : PendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, requestCode, intent, FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(this,"NotificationChannel")
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText((message)))
            .setContentIntent(pendingIntent)
            .setSmallIcon(notificationIcon)


        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            notify(requestCode,builder.build())
        }
    }
}