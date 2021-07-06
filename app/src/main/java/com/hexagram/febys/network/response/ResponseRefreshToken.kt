package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseRefreshToken(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
