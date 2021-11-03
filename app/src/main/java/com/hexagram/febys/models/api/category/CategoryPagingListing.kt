package com.hexagram.febys.models.api.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryPagingListing(
    val categories: List<Category>,
    @SerializedName("pagingInfo", alternate = ["pagination_info", "pagination_information"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable