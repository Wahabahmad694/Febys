package com.hexagram.febys.models.api.transaction

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionPagingListing(
    val transactions: List<Transaction>,
    @SerializedName("pagingInfo", alternate = ["pagination_info", "pagination_information"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable
