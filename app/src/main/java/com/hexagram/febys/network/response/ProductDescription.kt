package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ProductDescription(
    val id: Int,
    @SerializedName("content")
    val contentHTML: String,
    @SerializedName("attribute")
    val attribute: String
)