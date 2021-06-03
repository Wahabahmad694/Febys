package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("image")
    val images: List<String>,
    @SerializedName("_id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("__v")
    val v: Int
)
