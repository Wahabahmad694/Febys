package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.requests.VariantAndQuantityCart
import com.hexagram.febys.prefs.IPrefManger
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

    override fun getCartItem(variantId: Int) = cartDataSource.getCartItem(variantId)

    override suspend fun addCartItem(cartDTO: CartDTO) {
        cartDataSource.addCartItem(cartDTO)
        pushCart()
    }

    private suspend fun pushCart() {
        val cart: List<VariantAndQuantityCart> = cartDataSource.getCartForPush()
        val authToken = pref.getAccessToken()
        val requestPushCart = RequestPushCart(cart)
        backendService.pushCart(authToken, requestPushCart)
    }

    private suspend fun pullCart() {
        val authToken = pref.getAccessToken()
        val response = backendService.fetchCart(authToken)
        if (response is ApiResponse.ApiSuccessResponse) {
            val cart = response.data!!
            cartDataSource.mergeCart(cart)
        }
    }

    override suspend fun pullAndPushCart() {
        pullCart()
        pushCart()
    }

    override fun clearCart() {
        cartDataSource.clear()
    }
}