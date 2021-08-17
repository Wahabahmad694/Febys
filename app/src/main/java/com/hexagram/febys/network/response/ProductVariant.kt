package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ProductVariant(
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("video_url")
    val videoUrl: String,
    @SerializedName("sku_id")
    val skuId: String,
    @SerializedName("availability")
    val _availability: Int,
    @SerializedName("weight")
    val weight: String,
    @SerializedName("length")
    val length: String,
    @SerializedName("width")
    val width: String,
    @SerializedName("height")
    val height: String,
    @SerializedName("default")
    val _default: Int,
    @SerializedName("enabled")
    val _enabled: Int,
    @SerializedName("has_promotion")
    val _hasPromotion: Int,
    @SerializedName("promotion_start_date")
    val promotionStartDate: String,
    @SerializedName("promotion_end_date")
    val promotionEndDate: String,
    @SerializedName("promotion_price")
    val promotionPrice: String,
    @SerializedName("percentage_off")
    val percentageOff: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("variant_attributes", alternate = ["attributes"])
    val variantAttributes: List<ProductVariantAttributes>?
) {
    val isAvailable
        get() = _availability == 1

    val isDefault
        get() = _default == 1

    val isEnable
        get() = _enabled == 1

    val hasPromotion
        get() = _hasPromotion == 1

    fun getFirstVariantAttr(): ProductVariantAttributes? {
        if (variantAttributes.isNullOrEmpty()) return null
        return variantAttributes.sortedBy { it.id }[0]
    }

    fun getSecondVariantAttr(): ProductVariantAttributes? {
        if (variantAttributes.isNullOrEmpty() || variantAttributes.size < 2) return null
        return variantAttributes.sortedBy { it.id }[1]
    }
}

