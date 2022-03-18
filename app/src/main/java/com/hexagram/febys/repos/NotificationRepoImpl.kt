package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.notification.RemoteNotification
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.paginations.NotificationListingPagingSource
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NotificationRepoImpl @Inject constructor(
    private val pref: IPrefManger,
    private val backendService: FebysBackendService
) : INotificationRepo {
    override fun fetchNotification(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher
    ): Flow<PagingData<RemoteNotification>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            NotificationListingPagingSource(
                backendService,
                authToken,
                PagingListRequest().also { it.sorter = PagingListRequest.createDateSorter() }
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override suspend fun markRead(notificationId: String, dispatcher: CoroutineDispatcher) {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return
        backendService.markRead(authToken, notificationId)
    }

    override suspend fun clearNotificationBadge(dispatcher: CoroutineDispatcher) {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return
        backendService.clearNotificationBadge(authToken)
    }
}