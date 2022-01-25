package com.hexagram.febys.ui.screens.payment.models

import android.os.Parcelable
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payment(
    val paymentMethod: PaymentMethod,
    val paymentRequest: PaymentRequest
) : Parcelable
