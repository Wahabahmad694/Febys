package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class UniqueCategory(
    @SerializedName("image")
    val images: List<String>,
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("link")
    val link: String
)