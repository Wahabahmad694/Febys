package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.request.OrderRequest
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.vendor.VendorMessage
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.requests.SkuIdAndQuantity
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CartRepoImpl @Inject constructor(
    private val cartDataSource: ICartDataSource,
    private val backendService: FebysBackendService,
    private val pref: IPrefManger,
) : ICartRepo {

    override fun observeCart(): LiveData<List<CartDTO>> = cartDataSource.observeCart()

    override suspend fun updateCartItem(cartDTO: CartDTO) {
        cartDataSource.updateCartItem(cartDTO)
        pushCart()
    }

    override suspend fun removeFromCart(cartDTO: CartDTO) {
        cartDataSource.removeFromCart(cartDTO)
        pushCart()
    }

    override fun getCartItem(skuId: String) = cartDataSource.getCartItem(skuId)

    override suspend fun addCartItem(cartDTO: CartDTO) {
        cartDataSource.addCartItem(cartDTO)
        pushCart()
    }

    private suspend fun pushCart() {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return
        val cart: List<SkuIdAndQuantity> = cartDataSource.getCartSkuIdsAndQuantity()
        val requestPushCart = RequestPushCart(cart)
        val response = backendService.pushCart(authToken, requestPushCart)
        if (response is ApiResponse.ApiSuccessResponse) {
            val updatedCart = response.data!!
            cartDataSource.updateCart(updatedCart.cart)
        }
    }

    override suspend fun updateCart(cart: Cart) {
        cartDataSource.updateCart(cart)
    }

    override suspend fun refreshCart() = pushCart()

    override suspend fun fetchOrderInfo(
        voucher: String?, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Order>> = flow<DataState<Order>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow

        val orderRequest = getOrderRequest(voucher, null, listOf())
        if (orderRequest.items.isEmpty()) return@flow

        backendService.fetchOrderInfo(authToken, orderRequest)
            .onSuccess {
                cartDataSource.updateCart(data!!.order.toListOfCartDTO())
                emit(DataState.Data(data.order))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    override suspend fun placeOrder(
        transactionId: String,
        voucher: String?,
        vendorMessages: List<VendorMessage>
    ) = flow<DataState<Order>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow

        val orderRequest = getOrderRequest(voucher, transactionId, vendorMessages)
        if (orderRequest.items.isEmpty()) return@flow

        backendService.placeOrder(authToken, orderRequest)
            .onSuccess {
                emit(DataState.Data(data!!.order))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    override suspend fun doPayment(paymentRequest: PaymentRequest) = flow<DataState<Transaction>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow

        backendService.doWalletPayment(authToken, paymentRequest)
            .onSuccess {
                emit(DataState.Data(data!!.transaction))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    private fun getOrderRequest(
        voucher: String?,
        transactionId: String? = null,
        vendorMessages: List<VendorMessage>
    ): OrderRequest {
        val shippingAddress = pref.getDefaultShippingAddress()
        val items: List<SkuIdAndQuantity> = cartDataSource.getCartSkuIdsAndQuantity()
        val transaction = if (transactionId == null) null else listOf(transactionId)

        return OrderRequest(
            shippingAddress?.shippingDetail, voucher, items, vendorMessages, transaction
        )
    }

    override fun clearCart() {
        cartDataSource.clear()
    }
}