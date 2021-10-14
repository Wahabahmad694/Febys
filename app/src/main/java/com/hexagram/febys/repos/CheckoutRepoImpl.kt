package com.hexagram.febys.repos

import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.prefs.IPrefManger
import javax.inject.Inject

class CheckoutRepoImpl @Inject constructor(
    private val cartRepo: ICartRepo, private val pref: IPrefManger
) : ICheckoutRepo {

    override fun getDefaultShippingAddress(): ShippingAddress? {
        return pref.getDefaultShippingAddress()
    }

    override fun getDefaultPaymentMethod(): PaymentMethod? {
        return pref.getDefaultPaymentMethod()
    }
}