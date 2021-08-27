package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class CartProduct(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("delivery")
    val delivery: String,
    @SerializedName("tags")
    val _tags: String?,
    @SerializedName("fulfillment")
    val fulfillment: String,
    @SerializedName("is_complete")
    val _IsComplete: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("variant")
    val productVariant: CartProductVariant
) {

    val isComplete
        get() = _IsComplete == 1

    val tags
        get() = _tags?.split(",")?.toList()
}