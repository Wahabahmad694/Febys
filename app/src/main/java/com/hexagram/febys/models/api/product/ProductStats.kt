package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductStats(
    val _id: String,
    @SerializedName("units_sold")
    val unitsSold: Int,
    val sales: Price
) : Parcelable