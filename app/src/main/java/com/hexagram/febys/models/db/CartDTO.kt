package com.hexagram.febys.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.Variant
import com.hexagram.febys.models.api.vendor.Vendor
import java.util.*

@Entity
data class CartDTO constructor(
    val vendorId: String,
    val vendorImg: String,
    val vendorShopName: String,
    val vendorType: String,
    val productImg: String,
    val productName: String,
    @PrimaryKey
    val skuId: String,
    val originalPrice: Price,
    val price: Price,
    val hasPromotion: Boolean,
    var createdAt: Date,
    var quantity: Int,
) {
    companion object {
        fun fromVendorProductVariant(
            vendor: Vendor, product: Product, variant: Variant, quantity: Int = 1
        ): CartDTO {
            return CartDTO(
                vendorId = vendor._id,
                vendorImg = vendor.businessInfo.logo,
                vendorShopName = vendor.shopName,
                vendorType = vendor.role.name,
                productImg = variant.images[0],
                productName = product.name,
                skuId = variant.skuId,
                originalPrice = variant.originalPrice,
                price = variant.price,
                hasPromotion = variant.hasPromotion,
                createdAt = Date(),
                quantity = quantity
            )
        }
    }
}
