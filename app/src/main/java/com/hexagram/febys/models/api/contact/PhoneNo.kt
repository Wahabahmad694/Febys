package com.hexagram.febys.models.api.contact

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneNo(
    val _id: String,
    @SerializedName("country_code")
    val countryCode: String,
    val number: String
) : Parcelable