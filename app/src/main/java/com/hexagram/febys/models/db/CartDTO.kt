package com.hexagram.febys.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.network.response.ProductVariant
import com.hexagram.febys.network.response.CartVendor
import java.util.*

@Entity
data class CartDTO constructor(
    val vendorId: Int,
    val vendorName: String,
    val vendorStoreName: String,
    val vendorEmail: String,
    val vendorPhoneNo: String,
    val vendorImage: String,
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
    var quantity: Int,
    var createdAt: Date
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
        get() = _hasVariantPromotion == 1 || promotionPrice != null

    val variantPriceByQuantity
        get() = variantPrice.times(quantity)

    val promotionPriceByQuantity
        get() = promotionPrice?.toDouble()?.times(quantity) ?: 0.0

    companion object {
        fun fromVendorProductVariant(
            cartVendor: CartVendor? = null, oldProduct: OldProduct, variant: ProductVariant, quantity: Int = 1
        ): CartDTO {
            return CartDTO(
                vendorId = cartVendor?.id ?: 21,
                vendorName = cartVendor?.name ?: "Daniyal",
                vendorStoreName = cartVendor?.storeName ?: "Al-buraq",
                vendorEmail = cartVendor?.email ?: "Daniyal@gmail.com",
                vendorPhoneNo = cartVendor?.phoneNo ?: "90078601",
                vendorImage = "",
                vendorIndividualType = cartVendor?.individualVendorType ?: "B2B2C",
                productId = oldProduct.id,
                productName = oldProduct.name,
                productDelivery = oldProduct.delivery,
                _productTags = oldProduct._tags,
                productFulfillment = oldProduct.fulfillment,
                _isProductComplete = oldProduct._IsComplete,
                productCreatedAt = oldProduct.createdAt,
                productUpdatedAt = oldProduct.updatedAt,
                variantId = variant.id,
                variantPrice = variant.price,
                variantCurrencyCode = variant.currencyCode,
                variantImages = variant.images,
                _variantAvailability = variant._availability,
                _defaultVariant = variant._default,
                _hasVariantPromotion = variant._hasPromotion,
                promotionStartDate = variant.promotionStartDate,
                promotionEndDate = variant.promotionEndDate,
                promotionPrice = variant.promotionPrice,
                variantCreatedAt = variant.createdAt,
                variantUpdatedAt = variant.updatedAt,
                quantity = quantity,
                createdAt = Date()
            )
        }
    }
}
