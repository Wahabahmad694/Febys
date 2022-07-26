package com.hexagram.febys.models.api.suggestedSearch

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuggestedProduct(
    @SerializedName("_id")
    val id: String,
    val active: Boolean,
    @SerializedName("category_active")
    val categoryActive: Boolean,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("category_image")
    val categoryImage: String,
    @SerializedName("category_name")
    val categoryName: String,
    val domain: String,
    @SerializedName("has_promotion")
    val hasPromotion: Boolean,
    @SerializedName("original_price")
    val originalPrice: Price,
    val price: Price,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_image")
    val productImage: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("vendor_active")
    val vendorActive: Boolean,
    @SerializedName("vendor_id")
    val vendorId: String,
    @SerializedName("vendor_image")
    val vendorImage: String,
    @SerializedName("vendor_name")
    val vendorName: String
):Parcelable