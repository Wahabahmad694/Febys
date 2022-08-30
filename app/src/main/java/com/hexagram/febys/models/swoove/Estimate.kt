package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Estimate(
    @SerializedName("custom_start_date")
    val customStartDate: String?,
    @SerializedName("estimate_id")
    val estimateId: String,
    @SerializedName("estimate_type_details")
    val estimateTypeDetails: EstimateTypeDetails,
    @SerializedName("individual_pricing")
    val individualPricing: List<IndividualPricing>,
    val instructions: String,
    @SerializedName("is_aggregated")
    val isAggregated: Boolean,
    @SerializedName("time_string")
    val timeString: String,
    @SerializedName("total_distance")
    val totalDistance: Double,
    @SerializedName("total_pricing")
    val totalPricing: TotalPricing,

    var selected: Boolean = false
) : Parcelable