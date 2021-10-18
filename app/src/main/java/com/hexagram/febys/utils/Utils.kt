package com.hexagram.febys.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.view.ShippingAddress

object Utils {
    fun jsonFromShippingAddress(obj: ShippingAddress): String {
        return Gson().toJson(obj)
    }

    fun jsonToShippingAddress(json: String): ShippingAddress {
        return Gson().fromJson(json, ShippingAddress::class.java)
    }

    fun jsonFromPaymentMethod(obj: PaymentMethod): String {
        return Gson().toJson(obj)
    }

    fun jsonToPaymentMethod(json: String): PaymentMethod {
        return Gson().fromJson(json, PaymentMethod::class.java)
    }

    fun openLink(context: Context, link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}