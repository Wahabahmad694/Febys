package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.hexagram.febys.models.api.transaction.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentResponse(
    val transaction: Transaction
): Parcelable