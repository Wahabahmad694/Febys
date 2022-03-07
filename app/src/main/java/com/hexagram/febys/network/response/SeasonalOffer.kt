package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class SeasonalOffer(
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("offer")
    val offers: List<Offer>,
    @SerializedName("for")
    val _for: String,
    @SerializedName("__v")
    val v: Int
)

data class Offer(
    @SerializedName("image")
    val images: List<String>,
    @SerializedName("_id")
    val id: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("category_id")
    val categoryId: Int
)