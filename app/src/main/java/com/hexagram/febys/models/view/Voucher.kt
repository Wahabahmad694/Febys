package com.hexagram.febys.models.view

data class Voucher constructor(
    val id: Int,
    val type: String,
    val code: String,
    val expireDate: String,
    val amount: Double
)
