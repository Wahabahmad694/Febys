package com.hexagram.febys.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ProductVariant
import com.hexagram.febys.network.response.Vendor
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
        get() = _hasVariantPromotion == 1

    companion object {
        fun fromVendorProductVariant(
            vendor: Vendor? = null, product: Product, variant: ProductVariant, quantity: Int = 1
        ): CartDTO {
            return CartDTO(
                vendorId = vendor?.id ?: 21,
                vendorName = vendor?.name ?: "Daniyal",
                vendorStoreName = vendor?.storeName ?: "Al-buraq",
                vendorEmail = vendor?.email ?: "Daniyal@gmail.com",
                vendorPhoneNo = vendor?.phoneNo ?: "90078601",
                vendorImage = "",
                vendorIndividualType = vendor?.individualVendorType ?: "B2B2C",
                productId = product.id,
                productName = product.name,
                productDelivery = product.delivery,
                _productTags = product._tags,
                productFulfillment = product.fulfillment,
                _isProductComplete = product._IsComplete,
                productCreatedAt = product.createdAt,
                productUpdatedAt = product.updatedAt,
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
