package com.hexagram.febys.models.api.vouchers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoucherResponse(
    val vouchers: List<Voucher>
) : Parcelable