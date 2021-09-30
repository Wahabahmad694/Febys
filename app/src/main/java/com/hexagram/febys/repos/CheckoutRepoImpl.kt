package com.hexagram.febys.repos

import com.hexagram.febys.models.view.CheckoutModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckoutRepoImpl @Inject constructor(
    private val cartRepo: ICartRepo, private val pref: IPrefManger
) : ICheckoutRepo {

    override suspend fun fetchCheckoutModel(
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<CheckoutModel>> = flow<DataState<CheckoutModel>> {
        val checkoutModel = CheckoutModel(null)
        emit(DataState.Data(checkoutModel))
    }.flowOn(dispatcher)
}