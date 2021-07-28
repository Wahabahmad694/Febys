package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseToggleFav(
    @SerializedName("product_variant_ids")
    val variantIds: Set<Int>
)
