package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderReview constructor(
    @SerializedName("products_ratings")
    val productsRatings: List<ProductReview>,
    @SerializedName("vendor_rating")
    val vendorReview: VendorReview
) : Parcelable

