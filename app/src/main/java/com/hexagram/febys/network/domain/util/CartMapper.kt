package com.hexagram.febys.network.domain.util

import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.*
import javax.inject.Inject

class CartMapper @Inject constructor() : DomainMapper<List<CartDTO>, Cart?> {

    override fun mapToDomainModel(model: List<CartDTO>): Cart? {
        if (model.isEmpty()) return null

        val orderAmount = model.sumOf { it.variantPrice * it.quantity }

        val vendors = model.distinctBy { it.vendorId }.map { cartDTO ->
            val itemsByVendor = model.filter { cartDTO.vendorId == it.vendorId }
            val cartItems = itemsByVendor.map {

                val cartProductVariant = CartProductVariant(
                    it.variantId,
                    it.variantPrice,
                    it.variantCurrencyCode,
                    it.variantImages,
                    it._variantAvailability,
                    it._defaultVariant,
                    it._hasVariantPromotion,
                    it.promotionStartDate,
                    it.promotionEndDate,
                    it.promotionPrice,
                    it.variantCreatedAt,
                    it.variantUpdatedAt
                )

                val cartProduct = CartProduct(
                    it.productId,
                    it.productName,
                    it.productDelivery,
                    it._productTags,
                    it.productFulfillment,
                    it._isProductComplete,
                    it.productCreatedAt,
                    it.productUpdatedAt,
                    cartProductVariant
                )
                CartItem(it.quantity, cartProduct)
            }
            val vendorAmount = cartItems.sumOf { it.product.productVariant.price * it.quantity }

            Vendor(
                cartDTO.vendorId,
                cartDTO.vendorName,
                cartDTO.vendorStoreName,
                cartDTO.vendorEmail,
                cartDTO.vendorPhoneNo,
                cartDTO.vendorIndividualType,
                vendorAmount,
                cartItems
            )
        }

        val cartInfo = CartInfo(orderAmount = orderAmount, vendors = vendors)
        return Cart(cartInfo)
    }

    override fun mapFromDomainModel(domainModel: Cart?): List<CartDTO> {
        val list = mutableListOf<CartDTO>()
        domainModel?.info?.vendors?.forEach { vendor ->
            vendor.items.forEach { cartItem ->
                val product = cartItem.product
                val variant = product.productVariant
                val cartDTO = CartDTO(
                    vendorId = vendor.id,
                    vendorName = vendor.name,
                    vendorStoreName = vendor.storeName,
                    vendorEmail = vendor.email,
                    vendorPhoneNo = vendor.phoneNo,
                    vendorIndividualType = vendor.individualVendorType,
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
                    quantity = cartItem.quantity
                )
                list.add(cartDTO)
            }
        }
        return list
    }
}