package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OptimalEstimate(
    @SerializedName("custom_start_date")
    val customStartDate: String?,
    @SerializedName("delivery_type_message")
    val deliveryTypeMessage: String,
    @SerializedName("estimate_id")
    val estimateId: String,
    @SerializedName("estimate_type_details")
    val estimateTypeDetails: EstimateTypeDetails,
    @SerializedName("individual_pricing")
    val individualPricing: List<IndividualPricing>,
    val instructions: String,
    @SerializedName("time_string")
    val timeString: String,
    @SerializedName("total_distance")
    val totalDistance: Double,
    @SerializedName("total_pricing")
    val totalPricing: TotalPricing
):Parcelable