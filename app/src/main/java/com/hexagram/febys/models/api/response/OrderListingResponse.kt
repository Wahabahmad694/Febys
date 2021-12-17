package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListingResponse(
    val orders: List<Order>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable
