package com.hexagram.febys.network.requests

import com.google.gson.annotations.SerializedName

data class VariantAndQuantityCart(
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("qty", alternate = ["quantity"])
    val quantity: Int
)