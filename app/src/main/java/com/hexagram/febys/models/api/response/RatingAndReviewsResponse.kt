package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.RatingAndReviews
import kotlinx.parcelize.Parcelize

@Parcelize
data class RatingAndReviewsResponse(
    @SerializedName("ratings_and_reviews")
    val ratingAndReviews: List<RatingAndReviews>
) : Parcelable
