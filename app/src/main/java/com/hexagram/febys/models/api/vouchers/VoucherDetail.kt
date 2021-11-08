package com.hexagram.febys.models.api.vouchers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoucherDetail(
    @SerializedName("__v")
    val version: Int,
    @SerializedName("_id")
    val id: String,
    val active: Boolean,
    val amount: Double,
    @SerializedName("amount_type")
    val amountType: String,
    val code: String,
    @SerializedName("created_at")
    val creationTime: String,
    @SerializedName("end_date")
    val endingTime: String,
    @SerializedName("max_redeem_count")
    val maxRedeemAmount: Int,
    @SerializedName("max_user_redeem_count")
    val maxRedeemUser: Int,
    @SerializedName("start_date")
    val startingDate: String,
    val stats: Stats,
    @SerializedName("updated_at")
    val updatedTime: String
):Parcelable
