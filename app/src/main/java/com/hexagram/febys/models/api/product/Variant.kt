package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.parcelize.Parcelize

@Parcelize
data class Variant(
    val _id: String,
    @SerializedName("sku_id")
    val skuId: String,
    val default: Boolean,
    @SerializedName("has_promotion")
    val hasPromotion: Boolean,
    @SerializedName("planned_promotion")
    val plannedPromotion: Boolean,
    val availability: Boolean,
    @SerializedName("fulfillment_by_febys")
    val fulfillmentByFebys: Boolean,
    @SerializedName("free_delivery")
    val freeDelivery: Boolean,
    val attributes: List<Attr>,
    @SerializedName("original_price")
    val originalPrice: Price,
    val price: Price,
    val images: List<String>,
    val refund: Refund,
    val warranty: Warranty,
    val stats: ProductStats,
    val packaging: Packaging
) : Parcelable
