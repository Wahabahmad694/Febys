package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.Product
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    @SerializedName("qty", alternate = ["quantity"])
    var quantity: Int,
    val product: Product
) : Parcelable