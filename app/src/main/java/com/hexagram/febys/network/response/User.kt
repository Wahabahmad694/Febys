package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class User @JvmOverloads constructor(
    @SerializedName("id")
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    val phoneNo: String,
    @SerializedName("keycloak_id")
    val keycloakId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_verified")
    val isVerified: Int,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null
)
