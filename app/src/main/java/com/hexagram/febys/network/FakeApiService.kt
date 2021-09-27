package com.hexagram.febys.network

import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {
    private val addresses = mutableListOf(
        ShippingAddress(
            1,
            "Hillary",
            "Widanama",
            "Home",
            "Ghana",
            "Virtual Incubator, Busy Internet",
            null,
            "Airport Res, Area",
            "Ghana",
            "233321",
            "",
            true
        ),
        ShippingAddress(
            2,
            "Hillary",
            "Widanama",
            "Office",
            "Ghana",
            "No. 18 Third Close, Airport Res Area",
            null,
            "Airport Res, Area",
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

    suspend fun setAsDefault(id: Int) {
        delay(100)
        val defaultIndex = addresses.indexOfFirst { it.isDefault }
        if (defaultIndex != -1) {
            addresses[defaultIndex].isDefault = false
        }

        addresses.firstOrNull { it.id == id }?.isDefault = true
    }
}