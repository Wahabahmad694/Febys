package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.R
import com.hexagram.febys.models.api.pagination.Pagination
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.api.vendor.VendorPagingListing
import com.hexagram.febys.models.view.ListingHeader
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class VendorListingPagingSource constructor(
    private val authKey: String,
    private val service: FebysBackendService,
    private val isCelebrity: Boolean,
    private val request: PagingListRequest
) : BasePagingSource<Int, Any>() {
    private var fetchFollowingVendor = true
    private var addHeader = true

    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        request.pageNo = params.key ?: 1
        val response = if (isCelebrity) fetchCelebrities(authKey) else fetchVendors(authKey)
        return when (response) {
            is ApiResponse.ApiSuccessResponse -> {
                val vendorListing = mutableListOf<Any>()
                val vendorListingResponse = response.data!!.getResponse<VendorPagingListing>()
                val vendors = vendorListingResponse.vendors

                addHeaderIfNecessary(vendorListing, vendors)
                fetchAndAddFollowingVendor(vendorListing)

                val (prevKey, nextKey) = getPagingKeys(vendorListingResponse.pagingInfo)
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

    private suspend fun fetchCelebrities(authKey: String): ApiResponse<Pagination> {
        return if (authKey.isEmpty())
            service.fetchCelebrities(request.createQueryMap())
        else
            service.fetchRecommendCelebrities(authKey, request.createQueryMap())
    }

    private suspend fun fetchVendors(authKey: String): ApiResponse<Pagination> {
        return if (authKey.isEmpty())
            service.fetchVendors(request.createQueryMap())
        else
            service.fetchRecommendVendors(authKey, request.createQueryMap())
    }

    private fun addHeaderIfNecessary(
        vendorListing: MutableList<Any>, vendors: List<Vendor>
    ) {
        if (vendors.isEmpty()) return

        if (addHeader) {
            addHeader = false
            val headerRes = getUnfollowedHeaderStringRes()
            val exploreVendorHeader = ListingHeader(headerRes)
            vendorListing.add(0, exploreVendorHeader)
        }

        vendorListing.addAll(vendors)
    }

    private fun getUnfollowedHeaderStringRes(): Int {
        return if (isCelebrity) R.string.label_explore_markets else R.string.label_explore_stores
    }

    private suspend fun fetchAndAddFollowingVendor(vendorListing: MutableList<Any>) {
        if (!fetchFollowingVendor) return

        fetchFollowingVendor = false

        val headerRes = getFollowingVendorHeaderStringRes()
        val followingVendorHeader = ListingHeader(headerRes)

        val followingVendors = fetchFollowingVendors(authKey)
        if (followingVendors.isNotEmpty()) {
            vendorListing.addAll(0, followingVendors)
            vendorListing.add(0, followingVendorHeader)
        }
    }

    private fun getFollowingVendorHeaderStringRes(): Int {
        return if (isCelebrity) R.string.label_market_you_follow else R.string.label_store_you_follow
    }

    private suspend fun fetchFollowingVendors(authKey: String): List<Vendor> {
        if (authKey.isEmpty()) return emptyList()

        val followingVendorsResponse = if (isCelebrity)
            service.fetchFollowingCelebrities(authKey)
        else
            service.fetchFollowingVendors(authKey)

        return if (followingVendorsResponse is ApiResponse.ApiSuccessResponse) {
            followingVendorsResponse.data!!.vendors.onEach { it.isFollow = true }
        } else {
            emptyList()
        }
    }
}