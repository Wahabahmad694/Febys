package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.RatingAndReviews
import com.hexagram.febys.models.api.rating.Rating
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorStats(
    val products: Int,
    @SerializedName("units_sold")
    val unitsSold: Int,
    @SerializedName("answers_count")
    val answersCount: Int,
    val rating: Rating,
    @SerializedName("ratings_and_reviews")
    val ratingsAndReviews: RatingAndReviews,
    @SerializedName("value_score")
    val valueScore: List<Rating>,
    @SerializedName("pricing_score")
    val pricingScore: List<Rating>,
    @SerializedName("quality_score")
    val qualityScore: List<Rating>
) : Parcelable