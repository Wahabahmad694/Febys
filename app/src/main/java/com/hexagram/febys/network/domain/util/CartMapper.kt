package com.hexagram.febys.network.domain.util

import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.*
import java.util.*
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
                val cartProduct = cartItem.product
                val cartProductVariant = cartProduct.productVariant
                val cartDTO = CartDTO(
                    vendorId = vendor.id,
                    vendorName = vendor.name,
                    vendorStoreName = vendor.storeName,
                    vendorEmail = vendor.email,
                    vendorPhoneNo = vendor.phoneNo,
                    vendorImage = "",
                    vendorIndividualType = vendor.individualVendorType,
                    productId = cartProduct.id,
                    productName = cartProduct.name,
                    productDelivery = cartProduct.delivery,
                    _productTags = cartProduct._tags,
                    productFulfillment = cartProduct.fulfillment,
                    _isProductComplete = cartProduct._IsComplete,
                    productCreatedAt = cartProduct.createdAt,
                    productUpdatedAt = cartProduct.updatedAt,
                    variantId = cartProductVariant.id,
                    variantPrice = cartProductVariant.price,
                    variantCurrencyCode = cartProductVariant.currencyCode,
                    variantImages = cartProductVariant.images,
                    _variantAvailability = cartProductVariant._availability,
                    _defaultVariant = cartProductVariant._default,
                    _hasVariantPromotion = cartProductVariant._hasPromotion,
                    promotionStartDate = cartProductVariant.promotionStartDate,
                    promotionEndDate = cartProductVariant.promotionEndDate,
                    promotionPrice = cartProductVariant.promotionPrice,
                    variantCreatedAt = cartProductVariant.createdAt,
                    variantUpdatedAt = cartProductVariant.updatedAt,
                    quantity = cartItem.quantity,
                    createdAt = Date()
                )
                list.add(cartDTO)
            }
        }
        return list
    }
}