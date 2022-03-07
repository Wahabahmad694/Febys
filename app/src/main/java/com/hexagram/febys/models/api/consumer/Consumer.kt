package com.hexagram.febys.models.api.consumer

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.contact.PhoneNo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Consumer(
    val id: Int,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("last_name")
    var lastName: String,
    val email: String,
    @SerializedName("keycloak_id")
    val keycloakId: String,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("phone_number")
    var phoneNumber: PhoneNo,
    @SerializedName("units_purchased")
    val unitsPurchased: Int,
    @SerializedName("sale_value")
    val saleValue: Double,
    @SerializedName("sale_currency")
    val saleCurrency: String?,
    @SerializedName("is_verified")
    val isVerified: Int,
    @SerializedName("notificationsStatus")
    val notificationsStatus: Int,
    val deleted: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("profile_image")
    val profileImage: String?
) : Parcelable {
    val fullName
        get() = "$firstName $lastName"

    val notification
        get() = notificationsStatus == 1
}
