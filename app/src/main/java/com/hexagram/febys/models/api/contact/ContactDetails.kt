package com.hexagram.febys.models.api.contact

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactDetails(
    val _id: String,
    val email: String,
    val name: String,
    val address: String,
    @SerializedName("phone_number")
    val phoneNo: PhoneNo
) : Parcelable