package com.hexagram.febys.models.api.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchRequest(
    val searchStr: String?,
    var pageNo: Int = 1,
) : Parcelable{
    fun createQueryMap() = mapOf(
        "pageNo" to pageNo.toString(),
    )
}