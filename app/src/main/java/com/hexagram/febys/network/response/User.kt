package com.hexagram.febys.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User @JvmOverloads constructor(
    @SerializedName("id")
    var id: Int,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone_number")
    var phoneNo: String,
    @SerializedName("keycloak_id")
    val keycloakId: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("country_code")
    var countryCode: String?,
    @SerializedName("is_verified")
    val isVerified: Int,
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null
) : Parcelable {
    val fullName
        get() = "$firstName $lastName"
}
