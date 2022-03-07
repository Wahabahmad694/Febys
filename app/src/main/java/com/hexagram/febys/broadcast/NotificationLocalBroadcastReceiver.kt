package com.hexagram.febys.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationLocalBroadcastReceiver(
    private val onNewNotification: () -> Unit
) : BroadcastReceiver() {
    companion object {
        const val ACTION_RECEIVE_NOTIFICATION_BROADCAST = "receiveNotificationBroadCast"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(ACTION_RECEIVE_NOTIFICATION_BROADCAST)) onNewNotification.invoke()
    }
}