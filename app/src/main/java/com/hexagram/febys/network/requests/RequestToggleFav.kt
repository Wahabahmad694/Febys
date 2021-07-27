package com.hexagram.febys.network.requests

import com.google.gson.annotations.SerializedName

data class RequestToggleFav(
    @SerializedName("product_variant_ids")
    val variantIds: Set<Int>
)
