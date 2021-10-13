package com.hexagram.febys.repos

import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class PaymentMethodRepoImpl @Inject constructor(
    private val pref: IPrefManger
) : IPaymentMethodRepo {

    init {
        GlobalScope.launch {
            setAsDefault(1)
        }
    }

    override suspend fun fetchPaymentMethods(dispatcher: CoroutineDispatcher) =
        flow<DataState<List<PaymentMethod>>> {
            val response = FakeApiService.fetchPaymentMethods()
            response.onSuccess {
                emit(DataState.Data(data!!))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)


    override suspend fun setAsDefault(id: Int) {
        val paymentMethod = FakeApiService.setAsDefaultPaymentMethod(id)
        paymentMethod?.let { saveDefaultPaymentMethod(it) }
    }

    private fun saveDefaultPaymentMethod(paymentMethod: PaymentMethod) {
        pref.saveDefaultPaymentMethod(paymentMethod)
    }
}