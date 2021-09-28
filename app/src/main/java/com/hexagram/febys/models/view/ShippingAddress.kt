package com.hexagram.febys.models.view

import java.io.Serializable

data class ShippingAddress constructor(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val addressLabel: String,
    val region: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String?,
    val postalCode: String,
    val phoneNo: String,
    var isDefault: Boolean
) : Serializable {
    fun fullAddress(): String {
        return "$addressLine1,\n$city,\n$postalCode,\n$region"
    }
}