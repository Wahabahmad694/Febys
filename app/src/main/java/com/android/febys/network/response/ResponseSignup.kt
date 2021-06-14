package com.android.febys.network.response

import com.android.febys.dto.UserDTO
import com.google.gson.annotations.SerializedName

sealed class ResponseSignup {
    data class Success(
        @SerializedName("message")
        val message: String,
        @SerializedName("user")
        val userDTO: UserDTO
    ) : ResponseSignup()

    data class Fail(
        @SerializedName("message")
        val message: String,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("errors")
        val signupErrors: List<SignupError>
    ) : ResponseSignup()
}

data class SignupError(
    @SerializedName("field")
    val field: String,
    @SerializedName("error")
    val error: String
)
