package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.rating.Rating
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndorsementStats(
    @SerializedName("cancelled_orders")
    val cancelledOrders: Int,
    val products: Int,
    @SerializedName("units_sold")
    val unitsSold: Int,
    @SerializedName("answers_count")
    val answersCount: Int,
    val rating: Rating,
    val sales: Price,
) : Parcelable