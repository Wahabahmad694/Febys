package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseOtpVerification(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User
)