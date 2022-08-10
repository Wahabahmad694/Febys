package com.hexagram.febys.ui.screens.payment.models.feeSlabs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Programs(
    val gateway: String,
    val slab: Slab
) : Parcelable
