package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.notification.RemoteNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface INotificationRepo {
    fun fetchNotification(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<RemoteNotification>>

    suspend fun markRead(notificationId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun clearNotificationBadge(dispatcher: CoroutineDispatcher = Dispatchers.IO)
}