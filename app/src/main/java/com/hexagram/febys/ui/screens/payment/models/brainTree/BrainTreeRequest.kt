package com.hexagram.febys.ui.screens.payment.models.brainTree

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BraintreeRequest(
    val billingAmount: Double,
    val billingCurrency: String,
    val deviceData: String,
    val nonce: String,
    val rate: Double,
    val transactionFee: Double,
    val requestedAmount: Double,
    val requestedCurrency: String,
    val transactionType: String
) : Parcelable