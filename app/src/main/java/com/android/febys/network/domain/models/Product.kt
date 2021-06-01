package com.android.febys.network.domain.models

data class Product(
    val imgUrl: String,
    val productName: String,
    val productPrice: Double,
    val productPriceOff: Double,
    var isFav: Boolean = false
)
