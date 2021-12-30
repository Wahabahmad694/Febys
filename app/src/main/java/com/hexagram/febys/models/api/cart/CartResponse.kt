package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartResponse(
    val cart: Cart
) : Parcelable
