package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.view.VendorListing

data class ResponseVendorListing(
    @SerializedName("total_rows")
    val totalRows: Int,
    @SerializedName("vendors")
    val vendors: List<VendorListing.Vendor>,
    @SerializedName("pagination_info")
    val paginationInformation: PaginationInformation
)