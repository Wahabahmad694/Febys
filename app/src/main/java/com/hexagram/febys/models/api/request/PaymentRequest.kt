package com.hexagram.febys.models.api.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val purpose: String = "PRODUCT_PURCHASE"
) : Parcelable
