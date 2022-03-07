package com.hexagram.febys.notification

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

object FirebaseUtils {

    fun subscribeToTopic(topic: String) {
        Firebase.messaging.subscribeToTopic(topic).addOnCompleteListener { task ->
            var msg = "Notification subscribed to topic: $topic"
            if (!task.isSuccessful) {
                msg = "Notification subscription failed"
            }
            Log.d(FCMService.TAG, msg)
        }
    }

    fun unSubscribeToTopic(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic).addOnCompleteListener { task ->
            var msg = "Notification un-subscribed for topic: $topic"
            if (!task.isSuccessful) {
                msg = "Notification un-subscribe failed"
            }
            Log.d(FCMService.TAG, msg)
        }
    }
}