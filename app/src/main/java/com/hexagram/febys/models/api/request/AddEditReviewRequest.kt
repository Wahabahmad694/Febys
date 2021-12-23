package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.rating.Review
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddEditReviewRequest(
    val _id: String? = null,
    @SerializedName("products_ratings")
    val productsRatings: List<AddEditProductReviewRequest>,
    @SerializedName("vendor_rating")
    val vendorRating: AddEditProductVendorRequest
) : Parcelable

@Parcelize
data class AddEditProductReviewRequest(
    val _id: String? = null,
    @SerializedName("sku_id")
    val skuId: String,
    var score: Int,
    var review: Review
) : Parcelable

@Parcelize
data class AddEditProductVendorRequest(
    val _id: String? = null,
    @SerializedName("vendor_id")
    val vendorId: String,
    var title: String,
    @SerializedName("value_score")
    var valueScore: Int,
    @SerializedName("pricing_score")
    var pricingScore: Int,
    @SerializedName("quality_score")
    var qualityScore: Int,
    var review: Review
) : Parcelable
