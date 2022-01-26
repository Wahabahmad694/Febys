package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.transaction.TransactionPagingListing
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.ui.screens.payment.service.PaymentService

class TransactionPagingSource constructor(
    private val service: PaymentService,
    private val authToken: String,
    private val request: PagingListRequest
) : BasePagingSource<Int, Transaction>() {
    override fun getRefreshKey(state: PagingState<Int, Transaction>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.fetchTransactions(authToken, queryMap, request)) {
            is ApiResponse.ApiSuccessResponse -> {
                val transactions = response.data!!.getResponse<TransactionPagingListing>()
                val (prevKey, nextKey) = getPagingKeys(transactions.pagingInfo)
                LoadResult.Page(transactions.transactions, prevKey, nextKey)
            }
            is ApiResponse.ApiFailureResponse.Error -> {
                LoadResult.Error(Exception(response.message))
            }
            is ApiResponse.ApiFailureResponse.Exception -> {
                LoadResult.Error(Exception(response.exception))
            }
        }
    }
}