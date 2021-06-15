package com.android.febys.network.response

import com.android.febys.dto.User
import com.google.gson.annotations.SerializedName

sealed class ResponseOtpVerification {
    data class Success(
        @SerializedName("message")
        val message: String,
        @SerializedName("user")
        val user: User
    ) : ResponseOtpVerification()

    data class Fail(
        @SerializedName("message")
        val message: String,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("errors")
        val signupErrors: List<FieldError>
    ) : ResponseOtpVerification()
}