package com.hexagram.febys.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CartDTO(
    val vendorId: Int,
    val vendorName: String,
    val vendorStoreName: String,
    val vendorEmail: String,
    val vendorPhoneNo: String,
    val vendorIndividualType: String,
    val productId: Int,
    val productName: String,
    val productDelivery: String,
    val _productTags: String?,
    val productFulfillment: String,
    val _isProductComplete: Int,
    val productCreatedAt: String,
    val productUpdatedAt: String,
    @PrimaryKey(autoGenerate = true)
    val variantId: Int,
    val variantPrice: Double,
    val variantCurrencyCode: String,
    val variantImages: List<String>,
    val _variantAvailability: Int,
    val _defaultVariant: Int,
    val _hasVariantPromotion: Int?,
    val promotionStartDate: String?,
    val promotionEndDate: String?,
    val promotionPrice: String?,
    val variantCreatedAt: String,
    val variantUpdatedAt: String,
    val quantity: Int
) {
    val isProductComplete
        get() = _isProductComplete == 1

    val productTags
        get() = _productTags?.split(",")?.toList()

    val isVariantAvailable
        get() = _variantAvailability == 1

    val isDefaultVariant
        get() = _defaultVariant == 1

    val hasVariantPromotion
        get() = _hasVariantPromotion == 1
}
