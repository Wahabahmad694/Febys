package com.hexagram.febys.network.response

data class CartInfo(
    val id: Int = 0,
    val orderAmount: Double,
    val vendors: List<Vendor>
)