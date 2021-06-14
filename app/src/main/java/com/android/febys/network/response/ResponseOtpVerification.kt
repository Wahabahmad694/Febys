package com.android.febys.network.response

import com.android.febys.dto.UserDTO
import com.google.gson.annotations.SerializedName

sealed class ResponseOtpVerification {
    data class Success(
        @SerializedName("message")
        val message: String,
        @SerializedName("user")
        val userDTO: UserDTO
    ) : ResponseOtpVerification()

    data class Fail(
        @SerializedName("message")
        val message: String,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("errors")
        val signupErrors: List<OtpError>
    ) : ResponseOtpVerification()
}

data class OtpError(
    @SerializedName("field")
    val field: String,
    @SerializedName("error")
    val error: String
)