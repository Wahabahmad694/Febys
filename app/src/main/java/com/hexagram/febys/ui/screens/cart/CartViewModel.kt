package com.hexagram.febys.ui.screens.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.request.EstimateRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.api.vendor.VendorMessage
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.ICartRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import com.hexagram.febys.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
open class CartViewModel @Inject constructor(
    private val cartRepo: ICartRepo,
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {
    private val _downloadPdf = MutableLiveData<Event<DataState<ResponseBody>>>()
    val observerDownloadPdf: LiveData<Event<DataState<ResponseBody>>> = _downloadPdf

    fun observeCart(): LiveData<List<CartDTO>> = cartRepo.observeCart()

    fun updateCartItem(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.updateCartItem(cartDTO)
    }

    fun removeFromCart(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.removeFromCart(cartDTO)
    }

    fun addToCart(product: Product, skuId: String) {
        val existingCartDTO = cartRepo.getCartItem(skuId)
        if (existingCartDTO != null) {
            existingCartDTO.quantity += 1
            updateCartItem(existingCartDTO)
        } else {
            val variant = product.variants.first { it.skuId == skuId }
            val cartDTO = CartDTO.fromVendorProductVariant(
                vendor = product.vendor, product = product, variant = variant
            )
            addToCart(cartDTO)
        }
    }

    private fun addToCart(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.addCartItem(cartDTO)
    }

    fun sortListForCart(list: List<CartDTO>?): List<CartDTO>? {
        if (list == null) return list

        val sortedList = mutableListOf<CartDTO>()
        val vendorIds = list.map { it.vendorId }.toSet()

        vendorIds.forEach { vendorId ->
            val groupByVendor = list.filter { it.vendorId == vendorId }
            sortedList.addAll(groupByVendor.sortedByDescending { it.createdAt })
        }

        return sortedList
    }

    fun refreshCart() = viewModelScope.launch { cartRepo.refreshCart() }

    fun clearCart() = cartRepo.clearCart(true)

    fun fetchOrderInfo(voucher: String? = null) = cartRepo.fetchOrderInfo(voucher).asLiveData()

    fun placeOrder(
        transactions: List<Transaction>,
        voucher: String?,
        vendorMessages: List<VendorMessage>,
        estimate: EstimateRequest?
    ): LiveData<DataState<Order?>> =
        cartRepo.placeOrder(transactions, voucher, vendorMessages,estimate)
            .onStart {
                emit(DataState.Loading())
            }
            .onEach {
                delay(500)
                if (it is DataState.Data) clearCart()
            }.asLiveData()

    fun exportPdf() = viewModelScope.launch {
        _downloadPdf.postValue(Event(DataState.Loading()))
        cartRepo.downloadPdf().collect {
            _downloadPdf.value = Event(it)
        }
    }

}