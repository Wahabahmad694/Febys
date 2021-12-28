package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.rating.ProductReview
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct constructor(
    @SerializedName("qty", alternate = ["quantity"])
    var quantity: Int,
    val product: Product,
    @SerializedName("rating_and_review")
    var ratingAndReview: ProductReview? = null
) : Parcelable