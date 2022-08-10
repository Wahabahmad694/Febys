package com.hexagram.febys.ui.screens.payment.models.brainTree

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction(
    val clientToken: String
) : Parcelable

