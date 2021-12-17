package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class VendorPagingListing(
    val key: String,
    @SerializedName("vendors", alternate = ["celebs", "stores"])
    val vendors: List<Vendor>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable