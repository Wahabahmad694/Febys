package com.hexagram.febys.models.api.request

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class PagingListRequest constructor(
    @SerializedName("searchStr")
    var searchStr: String = "", // for product search
    @SerializedName("queryStr")
    var queryStr: String = "", // for all categories
    var chunkSize: Int = 10,
    var pageNo: Int = 1,
    var orderByCol: String = "created_at",
    var orderByType: String = "desc",
    @SerializedName("start_date")
    var startDate: String = "2001-10-04T14:59:17.238Z",
    @SerializedName("end_date")
    var endDate: String = "2022-12-04T14:59:17.238Z",
    var filters: JsonObject? = null,
    var sorter: JsonObject? = null,
) {
    fun createQueryMap() = mapOf(
        "chunkSize" to chunkSize.toString(),
        "pageNo" to pageNo.toString(),
        "orderByCol" to orderByCol,
        "orderByType" to orderByType,
        "start_date" to startDate,
        "end_date" to endDate,
    )

    companion object {
        fun createDateSorter(): JsonObject {
            val sorter = JsonObject()
            sorter.addProperty("created_at", "desc")
            return sorter
        }
    }
}