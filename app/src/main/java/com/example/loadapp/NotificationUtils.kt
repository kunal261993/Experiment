package com.example.loadapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    downloadStatus: String,
    fileName: String,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("downloadStatus", downloadStatus)
    contentIntent.putExtra("fileName", fileName)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(
        applicationContext,
        "download_file_channel"
    )
        .setSmallIcon(R.drawable.notification)
        .setContentTitle(fileName)
        .setContentText("Download " + downloadStatus)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.notification,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}