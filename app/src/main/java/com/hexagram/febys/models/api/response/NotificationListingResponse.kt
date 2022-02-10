package com.hexagram.febys.models.api.response

import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.notification.RemoteNotification
import com.hexagram.febys.models.api.pagination.PagingInfo

data class NotificationListingResponse(
    val notifications: List<RemoteNotification>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
)
