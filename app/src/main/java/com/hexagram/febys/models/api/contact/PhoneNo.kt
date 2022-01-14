package com.hexagram.febys.models.api.contact

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneNo(
    @SerializedName("country_code")
    val countryCode: String,
    var number: String
) : Parcelable