package com.hexagram.febys.repos

import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.prefs.IPrefManger
import javax.inject.Inject

class CheckoutRepoImpl @Inject constructor(
    private val pref: IPrefManger
) : ICheckoutRepo {

    override fun getDefaultShippingAddress(): ShippingAddress? {
        return pref.getDefaultShippingAddress()
    }
}