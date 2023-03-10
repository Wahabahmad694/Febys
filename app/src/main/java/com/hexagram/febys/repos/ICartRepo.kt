package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.request.EstimateRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.vendor.VendorMessage
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface ICartRepo {
    fun observeCart(): LiveData<List<CartDTO>>

    suspend fun updateCartItem(cartDTO: CartDTO)

    suspend fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(skuId: String): CartDTO?

    suspend fun addCartItem(cartDTO: CartDTO)

    fun clearCart(push: Boolean = false)

    suspend fun updateCart(cart: Cart)

    suspend fun refreshCart()

    fun fetchOrderInfo(
        voucher: String?, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Order?>>

    suspend fun downloadPdf(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseBody>>

    fun placeOrder(
        transactions: List<Transaction>,
        voucher: String?,
        vendorMessages: List<VendorMessage>,
        estimate: EstimateRequest?
    ): Flow<DataState<Order?>>
}