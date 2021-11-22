package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.shippingAddress.ShippingDetail
import com.hexagram.febys.models.api.vendor.VendorMessage
import com.hexagram.febys.network.requests.SkuIdAndQuantity
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderRequest constructor(
    @SerializedName("shipping_detail")
    val shippingDetail: ShippingDetail?,
    @SerializedName("voucher_code")
    var voucherCode: String?,
    val items: List<SkuIdAndQuantity>,
    val messages: List<VendorMessage>,
    @SerializedName("transaction_ids")
    var transaction: List<String>? = null,
) : Parcelable
