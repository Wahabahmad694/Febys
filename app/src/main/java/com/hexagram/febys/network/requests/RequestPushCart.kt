package com.hexagram.febys.network.requests

import com.google.gson.annotations.SerializedName

data class RequestPushCart(
    @SerializedName("cartItems")
    val cartItems: List<VariantAndQuantityCart>
)