package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductReview constructor(
    val _id: String? = null,
    @SerializedName("consumer_id")
    val consumerId: Int? = null,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("up_votes")
    val upVotes: MutableSet<String>? = null,
    @SerializedName("down_votes")
    val downVotes: MutableSet<String>? = null,
    val review: Review,
    var score: Double,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Parcelable