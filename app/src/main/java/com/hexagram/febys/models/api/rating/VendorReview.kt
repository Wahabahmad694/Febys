package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.consumer.Consumer
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorReview constructor(
    val _id: String? = null,
    var title: String = "",
    @SerializedName("consumer_id")
    val consumerId: Int? = null,
    val consumer: Consumer? = null,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("vendor_id")
    val vendorId: String,
    @SerializedName("value_score")
    var valueScore: Int = 5,
    @SerializedName("pricing_score")
    var pricingScore: Int = 5,
    @SerializedName("quality_score")
    var qualityScore: Int = 5,
    var review: Review,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
) : Parcelable