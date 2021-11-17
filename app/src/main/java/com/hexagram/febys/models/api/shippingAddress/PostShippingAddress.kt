package com.hexagram.febys.models.api.shippingAddress

import com.google.gson.annotations.SerializedName

data class PostShippingAddress(
    @SerializedName("shipping_detail")
    val postShippingDetail: PostShippingDetail
)
