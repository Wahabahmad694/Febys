package com.hexagram.febys.network.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestUpdateUser constructor(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phoneNo: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("notificationsStatus")
    val notificationsStatus: Int?,
) : Parcelable