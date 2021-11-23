package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.hexagram.febys.models.api.order.Order
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListingResponse(
    val orders: List<Order>
) : Parcelable
