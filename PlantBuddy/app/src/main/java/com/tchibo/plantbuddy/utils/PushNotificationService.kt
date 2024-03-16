package com.tchibo.plantbuddy.utils

import android.Manifest
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tchibo.plantbuddy.MainActivity

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send the token to the server

        Log.d("PushNotificationService", "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("PushNotificationService", "Message: ${remoteMessage.notification?.title} - ${remoteMessage.notification?.body}")
        Log.d("PushNotificationService", "Data: ${remoteMessage.data}")

        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Title"
        val message = remoteMessage.notification?.body ?: "Message"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data["data"] != null) {
            intent.putExtra("data", remoteMessage.data["data"])
        }

        intent.putExtra("title",title)
        intent.putExtra("body", message)

        // it should be unqiue when push comes.
        var requestCode = System.currentTimeMillis().toInt()
        var pendingIntent : PendingIntent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent =
                PendingIntent.getActivity(this, requestCode,intent, FLAG_MUTABLE)
        }else{
            pendingIntent =
                PendingIntent.getActivity(this, requestCode, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

//        pendingIntent.send()

        val builder = NotificationCompat.Builder(this,"Login").setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText((message)))
            .setContentIntent(pendingIntent)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)


        with(NotificationManagerCompat.from(this)){
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(requestCode,builder.build())
        }
    }
}