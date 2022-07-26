package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.models.api.suggestedSearch.SuggestedProduct
import com.hexagram.febys.network.SearchService
import com.hexagram.febys.network.adapter.ApiResponse

class SearchProductSuggestionPagingSource constructor(
    private val service: SearchService,
    private val request: SearchRequest,
    private val onTotalItems: (Long) -> Unit,
) : BasePagingSource<Int, SuggestedProduct>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SuggestedProduct> {


        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.searchProductsSuggestion(queryMap, request)) {
            is ApiResponse.ApiSuccessResponse -> {
                val searchProductResponse =
                    response.data?.listing!!
                onTotalItems.invoke(searchProductResponse.totalDocs)
                val (prevKey, nextKey) = getPagingKeys(searchProductResponse.pagingInfo)
                LoadResult.Page(searchProductResponse.search, prevKey, nextKey)

            }
            is ApiResponse.ApiFailureResponse.Error -> {
                LoadResult.Error(Exception(response.message))
            }
            is ApiResponse.ApiFailureResponse.Exception -> {
                LoadResult.Error(Exception(response.exception))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SuggestedProduct>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }


//    override suspend fun load(params: LoadParams<Int>): SearchSuggestionPagingListing {
//        request.pageNo = params.key ?: 1
//        val queryMap = request.createQueryMap()
//        return when (val response = service.searchProductsSuggestion(queryMap, request)) {
//            is ApiResponse.ApiSuccessResponse -> {
//                val searchProductResponse = response.data!!.getResponse<SearchSuggestionPagingListing>()
//                onSearchProductListingResponse?.invoke(searchProductResponse)
//                val (prevKey, nextKey) = getPagingKeys(searchProductResponse.pagingInfo)
//                PagingSource.LoadResult.Page(searchProductResponse.search, prevKey, nextKey)
//            }
//            is ApiResponse.ApiFailureResponse.Error -> {
//                PagingSource.LoadResult.Error(Exception(response.message))
//            }
//            is ApiResponse.ApiFailureResponse.Exception -> {
//                PagingSource.LoadResult.Error(Exception(response.exception))
//            }
//        }
//    }
}