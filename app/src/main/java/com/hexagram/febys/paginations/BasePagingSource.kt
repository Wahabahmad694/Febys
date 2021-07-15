package com.hexagram.febys.paginations

import androidx.paging.PagingSource
import com.hexagram.febys.network.response.PaginationInformation

abstract class BasePagingSource<Key : Any, Value : Any> : PagingSource<Key, Value>() {

    fun getPagingKeys(paginationInformation: PaginationInformation): Pair<Int?, Int?> {
        val nextPageNo = if (paginationInformation.pageNo < paginationInformation.totalPages) {
            paginationInformation.pageNo + 1
        } else {
            null
        }

        val previousPageNo = if (paginationInformation.pageNo == 1) {
            null
        } else {
            paginationInformation.pageNo - 1
        }

        return previousPageNo to nextPageNo
    }
}