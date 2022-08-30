package com.hexagram.febys.models.swoove

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TotalPricing(
    val currency_code: String,
    val currency_name: String,
    val currency_symbol: String,
    val discount: Int,
    val value: Int
):Parcelable