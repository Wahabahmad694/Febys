package com.hexagram.febys.network.requests

import com.google.gson.annotations.SerializedName

data class RequestPushCart(
    @SerializedName("items")
    val items: List<VariantAndQuantityCart>
)