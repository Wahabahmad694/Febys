package com.hexagram.febys.notification

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hexagram.febys.broadcast.NotificationLocalBroadcastReceiver
import com.hexagram.febys.models.api.notification.Notification
import com.hexagram.febys.models.api.notification.NotificationManager
import com.hexagram.febys.prefs.IPrefManger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject
    lateinit var pref: IPrefManger

    companion object {
        const val TAG = "Febys-Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isEmpty() || remoteMessage.notification == null) return

        Log.d(TAG, remoteMessage.data.toString())
        val title = remoteMessage.notification!!.title!!
        val body = remoteMessage.notification!!.body!!
        val notification = Notification.fromMap(remoteMessage.data) ?: return
        NotificationManager.sendNotification(this, title, body, notification)

        pref.saveNotificationCount(1)
        NotificationLocalBroadcastReceiver.sendBroadCast(this)
    }
}