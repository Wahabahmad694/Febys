package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class CartProductVariant(
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("availability")
    val _availability: Int,
    @SerializedName("default")
    val _default: Int,
    @SerializedName("has_promotion")
    val _hasPromotion: Int?,
    @SerializedName("promotion_start_date")
    val promotionStartDate: String?,
    @SerializedName("promotion_end_date")
    val promotionEndDate: String?,
    @SerializedName("promotion_price")
    val promotionPrice: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) {
    val isAvailable
        get() = _availability == 1

    val isDefault
        get() = _default == 1

    val hasPromotion
        get() = _hasPromotion == 1
}