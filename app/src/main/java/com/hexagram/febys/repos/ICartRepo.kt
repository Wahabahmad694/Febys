package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ICartRepo {
    fun observeCart(): LiveData<List<CartDTO>>

    suspend fun updateCartItem(cartDTO: CartDTO)

    suspend fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(skuId: String): CartDTO?

    suspend fun addCartItem(cartDTO: CartDTO)

    fun clearCart()

    suspend fun updateCart(cart: CartResponse)

    suspend fun refreshCart()

    suspend fun fetchOrderInfo(
        voucher: String?, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Order>>

    suspend fun placeOrder(transactionId: String, voucher: String?): Flow<DataState<Order>>

    suspend fun doPayment(paymentRequest: PaymentRequest): Flow<DataState<Transaction>>
}