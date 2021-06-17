package com.android.febys.network.response

import com.android.febys.dto.User
import com.google.gson.annotations.SerializedName

sealed class ResponseLogin {
    data class Success(
        @SerializedName("user")
        val user: User
    ) : ResponseLogin()

    data class Fail(
        @SerializedName("message")
        val message: String,
        @SerializedName("status")
        val status: Int?,
        @SerializedName("errors")
        val signupErrors: List<FieldError>
    ) : ResponseLogin()
}