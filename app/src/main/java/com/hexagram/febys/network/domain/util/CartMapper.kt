package com.hexagram.febys.network.domain.util

import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.*
import java.util.*
import javax.inject.Inject

class CartMapper @Inject constructor() : DomainMapper<List<CartDTO>, CartResponse?> {

    override fun mapToDomainModel(model: List<CartDTO>): CartResponse? {
        return null
    }

    override fun mapFromDomainModel(domainModel: CartResponse?): List<CartDTO> {
        val list = mutableListOf<CartDTO>()
        domainModel?.cart?.vendorProducts?.forEach { vendorProduct ->
            val vendor = vendorProduct.vendor
            val products = vendorProduct.products
            products.forEach { cartProduct ->
                val quantity = cartProduct.quantity
                val product = cartProduct.product
                val variant = product.variants[0]
                val cartDTO = CartDTO(
                    vendorId = vendor._id,
                    vendorImg = vendor.businessInfo.logo,
                    vendorShopName = vendor.shopName,
                    vendorType = vendor.role.name,
                    productImg = product.variants[0].images[0],
                    productName = product.name,
                    skuId = variant.skuId,
                    originalPrice = variant.originalPrice,
                    price = variant.price,
                    hasPromotion = variant.hasPromotion,
                    createdAt = Date(),
                    quantity = quantity
                )
                list.add(cartDTO)
            }
        }
        return list
    }
}