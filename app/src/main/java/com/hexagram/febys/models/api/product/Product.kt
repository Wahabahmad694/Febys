package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product constructor(
    val _id: String,
    @SerializedName("vendor_id")
    val vendorId: String,
    val active: Boolean,
    val brief: String,
    @SerializedName("category_active")
    val categoryActive: Boolean,
    @SerializedName("category_id")
    val categoryId: Int,
    val descriptions: List<Description>,
    val name: String,
    @SerializedName("update_product")
    val updateProduct: Boolean,
    @SerializedName("vendor_active")
    val vendorActive: Boolean,
    val variants: List<Variant>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
) : Parcelable

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
) : Parcelable