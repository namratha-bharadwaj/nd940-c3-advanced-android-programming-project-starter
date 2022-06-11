package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.udacity.R
import com.udacity.detail.DetailActivity
import com.udacity.main.MainActivity
import com.udacity.model.NotificationMessage


private lateinit var pendingIntent: PendingIntent
private lateinit var action: NotificationCompat.Action

fun NotificationManager.showNotification(
    notificationMessage: NotificationMessage,
    context: Context,
    notificationManager: NotificationManager
) {
    createNotificationChannel(notificationMessage, notificationManager)
    createNotificationAction(notificationMessage, context)
    val pendingIntent = createPendingIntent(notificationMessage, context)

    val downloadImage = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.ic_launcher_background
    )

    val builder = NotificationCompat.Builder(
        context,
        notificationMessage.channelID
    ).apply {
        setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        setContentTitle(notificationMessage.title)
        setContentText(notificationMessage.description)
        setAutoCancel(true)
        setContentIntent(pendingIntent)
        addAction(action)
        setLargeIcon(downloadImage)
    }

    notificationManager.notify(notificationMessage.id, builder.build())

}

private fun createNotificationChannel(
    notificationMessage: NotificationMessage,
    notificationManager: NotificationManager
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            notificationMessage.channelID,
            notificationMessage.channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }

        notificationChannel.apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Time for breakfast"
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }
}

private fun createNotificationAction(
    message: NotificationMessage,
    context: Context
) {
    val contentIntent = Intent(context, DetailActivity::class.java)
        .apply {
            putExtra(Constants.EXTRA_NOTIFICATION_ID, message.id)
            putExtra(Constants.EXTRA_DOWNLOADED_FILE, message.downloadUrl.downloadRepoTitle)
            putExtra(Constants.EXTRA_DOWNLOAD_STATUS, message.downloadStatus)
        }

    pendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(contentIntent)
        getPendingIntent(message.id, PendingIntent.FLAG_UPDATE_CURRENT)
    } as PendingIntent

    action =
        NotificationCompat.Action(
            R.drawable.ic_baseline_cloud_download_24,
            message.actionText,
            pendingIntent
        )

}

private fun createPendingIntent(
    message: NotificationMessage,
    context: Context
): PendingIntent {
    val contentIntent = Intent(context, MainActivity::class.java)
    return PendingIntent.getActivity(
        context,
        message.id,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
