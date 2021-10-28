package com.hexagram.febys.models.api.request

import com.google.gson.annotations.SerializedName

data class PagingListRequest constructor(
    @SerializedName("queryStr", alternate = ["searchStr"])
    var searchStr: String = "",
    var chunkSize: Int = 10,
    var pageNo: Int = 1,
    var orderByCol: String = "created_at",
    var orderByType: String = "asc",
    @SerializedName("start_date")
    var startDate: String = "2001-10-04T14:59:17.238Z",
    @SerializedName("end_date")
    var endDate: String = "2022-12-04T14:59:17.238Z",
    var filters: Filters = Filters()
) {
    fun createQueryMap() = mapOf(
        "chunkSize" to chunkSize.toString(),
        "pageNo" to pageNo.toString(),
        "orderByCol" to orderByCol,
        "orderByType" to orderByType,
        "start_date" to startDate,
        "end_date" to endDate,
    )
}