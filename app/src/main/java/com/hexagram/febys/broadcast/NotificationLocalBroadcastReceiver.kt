package com.hexagram.febys.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotificationLocalBroadcastReceiver(
    private val onNewNotification: () -> Unit
) : BroadcastReceiver() {
    companion object {
        const val ACTION_RECEIVE_NOTIFICATION_BROADCAST = "receiveNotificationBroadCast"

        fun sendBroadCast(context: Context) {
            Intent().also { intent ->
                intent.action = ACTION_RECEIVE_NOTIFICATION_BROADCAST
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(ACTION_RECEIVE_NOTIFICATION_BROADCAST)) onNewNotification.invoke()
    }
}