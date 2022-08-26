package com.hexagram.febys.ui.screens.checkout

import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.swoove.Estimate
import com.hexagram.febys.repos.ICartRepo
import com.hexagram.febys.repos.ICheckoutRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.cart.CartViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val checkoutRepo: ICheckoutRepo,
    cartRepo: ICartRepo,
    productRepo: IProductRepo,
) : CartViewModel(cartRepo, productRepo) {

    var estimate: Estimate? = null

    fun getDefaultShippingAddress(): ShippingAddress? {
        return checkoutRepo.getDefaultShippingAddress()
    }

    fun cancelAllRunningJobs() {
        viewModelScope.cancel()
    }
}