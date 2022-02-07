package com.hexagram.febys.network.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestReturnOrder(
    @SerializedName("return_items")
    val returnItems: List<SkuIdAndQuantity>,
    val reason: String,
    val comments: String,
): Parcelable
