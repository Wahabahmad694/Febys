package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("user")
    val user: User
)