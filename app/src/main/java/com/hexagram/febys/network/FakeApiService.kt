package com.hexagram.febys.network

import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {
    private val addresses = mutableListOf<ShippingAddress>(
        ShippingAddress(
            1,
            "Hillary",
            "Africa",
            "Home",
            "Ghana",
            "Virtual Incubator, Busy Internet",
            null,
            "Ghana",
            "233321",
            "",
            true
        ),
        ShippingAddress(
            2,
            "Hillary",
            "Africa",
            "Office",
            "Ghana",
            "No. 18 Third Close, Airport Res Area",
            null,
            "Ghana",
            "233321",
            "",
            false
        )
    )

    suspend fun fetchShippingAddress(authToken: String): ApiResponse<List<ShippingAddress>> {
        delay(1000)
        return ApiResponse.ApiSuccessResponse(Response.success(addresses))
    }

    suspend fun fetchFollowingVendor(): List<VendorListing.FollowingVendor> {
        delay(500)
        return listOf(
            VendorListing.FollowingVendor(1, "Deal-Train", "Official", 2.5f, "", true),
            VendorListing.FollowingVendor(2, "Deal-Train", "Official", 2.5f, "", true)
        )
    }
}