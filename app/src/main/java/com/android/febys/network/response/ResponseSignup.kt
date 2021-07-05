package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: User
)
