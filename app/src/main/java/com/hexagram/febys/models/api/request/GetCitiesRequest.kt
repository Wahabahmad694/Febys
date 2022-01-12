package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetCitiesRequest(
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("state_code")
    val stateCode: String
): Parcelable