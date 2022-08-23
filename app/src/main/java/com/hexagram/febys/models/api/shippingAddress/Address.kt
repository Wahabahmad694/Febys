package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val city: String?,
    @SerializedName("country_code")
    val countryCode: String,
    val state: String?,
    val street: String,
    @SerializedName("zip_code")
    val zipCode: String?,
    val location: Coordinates
):Parcelable{
    fun fullAddress(): String {
        return "$street, $city, $state\n$zipCode,\n$countryCode"
    }

    fun singleLineAddress():String{
        return "$street, $city, $state, $zipCode, $countryCode."
    }
}