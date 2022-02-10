package com.hexagram.febys.models.api.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hexagram.febys.R
import com.hexagram.febys.ui.MainActivity

object NotificationManager {
    object ID {
        const val FEBYS_PLUS = 111
        const val ORDER = 222
        const val QA = 333
    }

    object ChannelId {
        const val FEBYS_PLUS = "febysPlusNotificationChannelId"
        const val ORDER = "orderNotificationChannelId"
        const val QA = "qaNotificationChannelId"
    }

    fun createAllChannels(context: Context) {
        createChannel(
            context, ChannelId.FEBYS_PLUS,
            context.getString(R.string.febys_plus_notification_channel_name),
            context.getString(R.string.febys_plus_notification_channel_description)
        )

        createChannel(
            context, ChannelId.ORDER,
            context.getString(R.string.order_notification_channel_name),
            context.getString(R.string.order_notification_channel_description)
        )

        createChannel(
            context, ChannelId.QA,
            context.getString(R.string.qa_notification_channel_name),
            context.getString(R.string.qa_notification_channel_description)
        )
    }

    fun sendNotification(
        context: Context, title: String, content: String, notification: Notification
    ) {
        when (notification) {
            is Notification.FebysPlus -> {
                sendNotification(context, title, content, notification)
            }
            is Notification.Order -> {
                sendNotification(context, title, content, notification)
            }
            is Notification.QA -> {
                sendNotification(context, title, content, notification)
            }
        }
    }

    private fun sendNotification(
        context: Context, title: String, content: String, notification: Notification.FebysPlus
    ) {
        sendNotification(
            context,
            ChannelId.FEBYS_PLUS,
            title,
            content,
            context.getString(R.string.febys_plus_notification_channel_name),
            context.getString(R.string.febys_plus_notification_channel_description),
            ID.FEBYS_PLUS,
            getPendingIntent(context)
        )
    }

    private fun sendNotification(
        context: Context, title: String, content: String, notification: Notification.Order
    ) {
        sendNotification(
            context,
            ChannelId.ORDER,
            title,
            content,
            context.getString(R.string.order_notification_channel_name),
            context.getString(R.string.order_notification_channel_description),
            ID.ORDER,
            getPendingIntent(context)
        )
    }

    private fun sendNotification(
        context: Context, title: String, content: String, notification: Notification.QA
    ) {
        sendNotification(
            context,
            ChannelId.QA,
            title,
            content,
            context.getString(R.string.qa_notification_channel_name),
            context.getString(R.string.qa_notification_channel_description),
            ID.QA,
            getPendingIntent(context)
        )
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun sendNotification(
        context: Context,
        channelId: String,
        title: String,
        content: String,
        channelName: String,
        channelDescription: String,
        notificationId: Int,
        pendingIntent: PendingIntent
    ) {
        val builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_febys_small)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

        createChannel(context, channelId, channelName, channelDescription)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    private fun createChannel(
        context: Context, channelId: String, channelName: String, channelDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}