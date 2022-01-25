package com.hexagram.febys.models.api.transaction

import android.graphics.Color
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("requested_currency")
    val requestedCurrency: String,
    @SerializedName("requested_amount")
    val requestedAmount: Double,
    @SerializedName("conversion_rate")
    val conversionRate: Double,
    @SerializedName("billing_currency")
    val billingCurrency: String,
    @SerializedName("billing_amount")
    val billingAmount: Double,
    val source: String,
    @SerializedName("external_ref_no")
    val externalRefNo: String?,
    val status: String,
    val purpose: String,
    val _id: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_at")
    val createdAt: String,
) : Parcelable {
    val statusColor
        get() = when (status) {
            "REJECTED" -> {
                Color.parseColor("#FFCFCF")
            }
            "REVERSAL_CLAIMED" -> {
                Color.parseColor("#FFDBB9")
            }
            else -> {
                Color.parseColor("#D1FFB5")  // "CLAIMED"
            }
        }
}