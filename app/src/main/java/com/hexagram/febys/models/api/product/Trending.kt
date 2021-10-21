package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trending(
    @SerializedName("top_performers")
    val topPerformers: List<TopPerformer>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable {

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        topPerformers.forEach {
            list.addAll(it.product)
        }
        return list
    }
}
