package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.consumer.Consumer
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorReview constructor(
    val _id: String? = null,
    @SerializedName("consumer_id")
    val consumerId: Int? = null,
    val consumer: Consumer? = null,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("vendor_id")
    val vendorId: String,
    @SerializedName("value_score")
    var valueScore: Double = 5.0,
    @SerializedName("pricing_score")
    var pricingScore: Double = 5.0,
    @SerializedName("quality_score")
    var qualityScore: Double = 5.0,
    var review: Review,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Parcelable