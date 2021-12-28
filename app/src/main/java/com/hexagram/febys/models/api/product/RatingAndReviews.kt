package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.rating.Review
import kotlinx.parcelize.Parcelize

@Parcelize
data class RatingAndReviews(
    val _id: String,
    @SerializedName("consumer_id")
    val consumerId: Int,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("down_votes")
    val downVotes: MutableSet<String>,
    @SerializedName("up_votes")
    val upVotes: MutableSet<String>,
    @SerializedName("product_id")
    val productId: String,
    val score: Double,
    val consumer: Consumer,
    val review: Review,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("pricing_score")
    val pricingScore: Double,
    @SerializedName("quality_score")
    val qualityScore: Double,
    @SerializedName("value_score")
    val valueScore: Double,
    val title: String,
) : Parcelable