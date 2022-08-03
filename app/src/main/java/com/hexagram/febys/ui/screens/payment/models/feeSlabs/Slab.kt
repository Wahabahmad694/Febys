package com.hexagram.febys.ui.screens.payment.models.feeSlabs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Slab(
    val _id: String,
    val fixed: Double,
    val from: Double,
    val currency: String,
    val isActive: Boolean,
    val percentage: Double,
    val to: Double,
    val type: String
) : Parcelable
