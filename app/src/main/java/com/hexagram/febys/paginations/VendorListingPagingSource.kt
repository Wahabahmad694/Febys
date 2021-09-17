package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.R
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.ResponseVendorListing

class VendorListingPagingSource constructor(
    private val authKey: String,
    private val service: FebysBackendService,
    private val isCelebrity: Boolean,
    private val request: RequestOfPagination
) : BasePagingSource<Int, VendorListing>() {
    private var fetchFollowingVendor = true
    private var addHeader = true

    override fun getRefreshKey(state: PagingState<Int, VendorListing>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VendorListing> {
        request.pageNo = params.key ?: 1
        val req = mapOf("chunkSize" to request.chunkSize, "pageNo" to request.pageNo)

        return when (val response =
            if (isCelebrity) service.fetchCelebrities(req) else service.fetchVendors(req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val vendorListing = mutableListOf<VendorListing>()
                val vendorListingResponse = response.data!!.getResponse<ResponseVendorListing>()

                if (vendorListingResponse.vendors.isNotEmpty()) {
                    if (addHeader) {
                        addHeader = false
                        val headerRes =
                            if (isCelebrity) R.string.label_explore_markets else R.string.label_explore_stores
                        val exploreVendorHeader = VendorListing.VendorListingHeader(headerRes)
                        vendorListing.add(0, exploreVendorHeader)
                    }
                    vendorListing.addAll(vendorListingResponse.vendors)
                }

                if (fetchFollowingVendor) {
                    fetchFollowingVendor = false
                    val headerRes =
                        if (isCelebrity) R.string.label_market_you_follow else R.string.label_store_you_follow
                    val followingVendorHeader = VendorListing.VendorListingHeader(headerRes)
                    val followingVendors = fetchFollowingVendors(authKey)

                    if (followingVendors.isNotEmpty()) {
                        vendorListing.addAll(0, followingVendors)
                        vendorListing.add(0, followingVendorHeader)
                    }
                }

                val (prevKey, nextKey) = getPagingKeys(vendorListingResponse.paginationInformation)
                LoadResult.Page(vendorListing, prevKey, nextKey)
            }
            is ApiResponse.ApiFailureResponse.Error -> {
                LoadResult.Error(Exception(response.message))
            }
            is ApiResponse.ApiFailureResponse.Exception -> {
                LoadResult.Error(Exception(response.exception))
            }
        }
    }

    private suspend fun fetchFollowingVendors(authKey: String): List<VendorListing.FollowingVendor> {
        if (authKey.isEmpty()) return emptyList()

        val followingVendorsResponse =
            if (isCelebrity) service.fetchFollowingCelebrities(authKey)
            else service.fetchFollowingVendors(authKey)

        return if (followingVendorsResponse is ApiResponse.ApiSuccessResponse) {
            VendorListing.FollowingVendor.fromVendors(followingVendorsResponse.data!!.vendors)
        } else {
            emptyList()
        }
    }
}