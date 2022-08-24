package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceDetails(
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("currency_name")
    val currencyName: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    val discount: Int,
    val value: Int
):Parcelable