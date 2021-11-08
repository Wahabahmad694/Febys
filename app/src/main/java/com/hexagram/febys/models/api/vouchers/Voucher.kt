package com.hexagram.febys.models.api.vouchers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Voucher constructor(
    @SerializedName("__v")
    val version: Int,

    @SerializedName("_id")
    val id: String,

    @SerializedName("consumer_id")
    val consumerID: Int,
    @SerializedName("created_at")
    val createdTime: String,

    @SerializedName("updated_at")
    val updatedTime: String,

    val voucher: VoucherDetail,
    val voucherActive: Boolean
) : Parcelable
