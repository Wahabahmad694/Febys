package com.hexagram.febys.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.view.PaymentMethod
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    object DateTime {
        const val FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val FORMAT_MONTH_DATE_YEAR = "MMM dd, yyyy"
        const val FORMAT_MONTH_DATE_YEAR_HOUR_MIN = "MMM dd, yyyy-hh:mm"

        fun formatDate(dateString: String, pattern: String = FORMAT_MONTH_DATE_YEAR): String {
            val dateFormat = SimpleDateFormat(FORMAT_ISO, Locale.US)
            val date = dateFormat.parse(dateString) ?: return ""
            val requiredDateFormat = SimpleDateFormat(pattern, Locale.US)
            return requiredDateFormat.format(date)
        }
    }

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