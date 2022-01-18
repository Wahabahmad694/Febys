package com.hexagram.febys.ui.screens.payment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayStackPaymentResponse(
    @SerializedName("transaction_request")
    val transactionRequest: PayStackTransactionRequest
) : Parcelable

@Parcelize
data class PayStackTransactionRequest(
    @SerializedName("authorization_url")
    val authorizationUrl: String,
    @SerializedName("access_code")
    val accessCode: String,
    val reference: String
) : Parcelable
