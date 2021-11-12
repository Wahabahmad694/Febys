package com.hexagram.febys.network.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SkuIdAndQuantity(
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("qty", alternate = ["quantity"])
    val quantity: Int
) : Parcelable