package com.android.febys.models.requests

data class RequestAllCategories(
    var chunkSize: Int = 10,
    var pageNo: Int = 1,
    var queryStr: String = "",
    var orderByCol: String = "created_at",
    var orderByType: String = "asc"
)