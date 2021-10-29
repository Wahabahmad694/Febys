package com.hexagram.febys.models.api.cart

import com.hexagram.febys.models.api.product.Product

data class CartProduct(
    var quantity: Int,
    val product: Product
)