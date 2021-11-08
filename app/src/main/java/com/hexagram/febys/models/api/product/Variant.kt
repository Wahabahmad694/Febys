package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.parcelize.IgnoredOnParcel
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
    @SerializedName("images")
    val _images: List<String>,
    val refund: Refund,
    val warranty: Warranty,
    val stats: ProductStats,
    val packaging: Packaging
) : Parcelable {

    @IgnoredOnParcel
    val images: List<String>
        get() {
            return if (_images.isNotEmpty()) _images else listOf("")
        }

    fun getFirstVariantAttr(): Attr? {
        if (attributes.isNullOrEmpty()) return null
        return attributes.sortedBy { it._id }[0]
    }

    fun getSecondVariantAttr(): Attr? {
        if (attributes.isNullOrEmpty() || attributes.size < 2) return null
        return attributes.sortedBy { it._id }[1]
    }
}
