package com.hexagram.febys.ui.screens.notifications

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.notification.RemoteNotification
import com.hexagram.febys.repos.INotificationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepo: INotificationRepo
) : BaseViewModel() {
    private var notificationListOldOrderListing: Flow<PagingData<RemoteNotification>>? = null

    fun fetchNotificationList(refresh: Boolean): Flow<PagingData<RemoteNotification>> {
        if (notificationListOldOrderListing == null || refresh) {
            notificationListOldOrderListing = notificationRepo.fetchNotification(viewModelScope)
        }

        return notificationListOldOrderListing!!
    }
}