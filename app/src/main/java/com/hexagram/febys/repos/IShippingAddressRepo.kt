package com.hexagram.febys.repos

import com.hexagram.febys.models.api.countries.CountryResponse
import com.hexagram.febys.models.api.shippingAddress.PostShippingAddress
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IShippingAddressRepo {
    suspend fun fetchShippingAddressRepo(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<ShippingAddress>>>

    suspend fun setAsDefault(shippingAddress: ShippingAddress)

    suspend fun updateShippingAddress(
        shippingAddress: PostShippingAddress, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>

    suspend fun addShippingAddress(
        shippingAddress: PostShippingAddress, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>

    suspend fun deleteShippingAddress(
        shippingAddress: ShippingAddress,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>

    suspend fun fetchCountries(
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<CountryResponse>>


}