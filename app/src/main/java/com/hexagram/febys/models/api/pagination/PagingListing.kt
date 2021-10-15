package com.hexagram.febys.models.api.pagination

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.Product
import kotlinx.parcelize.Parcelize


abstract class PagingListing constructor(
    @SerializedName("pagination_info")
    open val pagingInfo: PagingInfo,
    @SerializedName("pagination_info")
    open val totalRows: Int
) : Parcelable
