package com.android.febys.network.requests

import com.google.gson.annotations.SerializedName

data class RequestSignup(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    val phoneNo: String,
    @SerializedName("password")
    val password: String
)
