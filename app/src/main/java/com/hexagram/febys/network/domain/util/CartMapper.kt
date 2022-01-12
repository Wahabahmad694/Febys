package com.hexagram.febys.network.domain.util

import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.db.CartDTO
import java.util.*
import javax.inject.Inject

class CartMapper @Inject constructor() : DomainMapper<List<CartDTO>, Cart?> {

    override fun mapToDomainModel(model: List<CartDTO>): Cart? {
        return null
    }

    override fun mapFromDomainModel(domainModel: Cart?): List<CartDTO> {
        return mapFromVendorProducts(domainModel?.vendorProducts)
    }

    fun mapFromVendorProducts(vendorProducts: MutableList<VendorProducts>?): List<CartDTO> {
        val list = mutableListOf<CartDTO>()
        vendorProducts?.forEach { vendorProduct ->
            val vendor = vendorProduct.vendor
            val products = vendorProduct.products
            products.forEach { cartProduct ->
                val quantity = cartProduct.quantity
                val product = cartProduct.product
                val variant = product.variants[0]
                val cartDTO = CartDTO(
                    productId = product._id,
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