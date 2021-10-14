package com.hexagram.febys.models.view

import androidx.annotation.DrawableRes

data class PaymentMethod(
    val id: Int,
    val name: String,
    @DrawableRes val img: Int,
    var isDefault: Boolean
)
