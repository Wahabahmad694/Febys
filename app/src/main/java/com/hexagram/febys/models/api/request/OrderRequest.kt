package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.vendor.VendorMessage
import com.hexagram.febys.network.requests.SkuIdAndQuantity
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderRequest(
//    var shippingDetail: ShippingAddress?,
    @SerializedName("voucher_code")
    var voucherCode: String?,
    val items: List<SkuIdAndQuantity>,
    val messages: List<VendorMessage>
) : Parcelable
