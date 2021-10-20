package com.hexagram.febys.paginations

import androidx.paging.PagingSource
import com.hexagram.febys.models.api.pagination.PagingInfo

abstract class BasePagingSource<Key : Any, Value : Any> : PagingSource<Key, Value>() {

    fun getPagingKeys(paginationInfo: PagingInfo): Pair<Int?, Int?> {
        val nextPageNo = if (paginationInfo.pageNo < paginationInfo.totalPages) {
            paginationInfo.pageNo + 1
        } else {
            null
        }

        val previousPageNo = if (paginationInfo.pageNo == 1) {
            null
        } else {
            paginationInfo.pageNo - 1
        }

        return previousPageNo to nextPageNo
    }
}