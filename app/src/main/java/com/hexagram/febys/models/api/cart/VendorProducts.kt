package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.hexagram.febys.models.api.vendor.Vendor
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorProducts(
    val vendor: Vendor,
    val products: MutableList<CartProduct>
) : Parcelable
