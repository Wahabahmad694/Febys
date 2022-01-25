package com.hexagram.febys.ui.screens.payment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ConversionRateResponse (
    @SerializedName("conversion_rate")
    val conversionRate: Double
): Parcelable