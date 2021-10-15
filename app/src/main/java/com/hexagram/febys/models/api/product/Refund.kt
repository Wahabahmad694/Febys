package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Refund(
    val _id: String,
    val refundable: Boolean,
    val policy: String
) : Parcelable