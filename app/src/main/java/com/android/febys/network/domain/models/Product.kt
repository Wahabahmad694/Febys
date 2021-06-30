package com.android.febys.network.domain.models

data class Product constructor(
    val id: Int,
    val imgUrl: String,
    val productName: String,
    val shortDescription: String,
    val productPrice: Double,
    val productPriceOff: Double,
    var isFav: Boolean = false
)
