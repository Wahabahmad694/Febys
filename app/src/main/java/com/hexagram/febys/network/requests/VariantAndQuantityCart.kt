package com.hexagram.febys.network.requests

import com.google.gson.annotations.SerializedName

data class VariantAndQuantityCart(
    @SerializedName("variant_id")
    val variantId: Int,
    @SerializedName("quantity")
    val quantity: Int
)