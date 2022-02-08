package com.hexagram.febys.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.Wallet
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val DEFAULT_CURRENCY = "GHS"

    object DateTime {
        const val FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val FORMAT_MONTH_DATE_YEAR = "MMMM dd, yyyy"
        const val FORMAT_MONTH_DATE_YEAR_HOUR_MIN = "MMM dd, yyyy-hh:mm"

        const val MIN_30_IN_MILLI = 30 * 60 * 1000

        fun formatDate(dateString: String, pattern: String = FORMAT_MONTH_DATE_YEAR): String {
            return try {
                val utcDateFormat = SimpleDateFormat(FORMAT_ISO, Locale.getDefault())
                utcDateFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = utcDateFormat.parse(dateString)!!
                val localTimeFormat = SimpleDateFormat(pattern, Locale.getDefault())
                localTimeFormat.timeZone = TimeZone.getDefault()
                localTimeFormat.format(date)
            } catch (e: Exception) {
                ""
            }
        }

        fun getRemainingMilliFrom30Min(dateString: String): Long {
            return try {
                val utcToLocal = formatDate(dateString, FORMAT_ISO)
                val localTimeFormat = SimpleDateFormat(FORMAT_ISO, Locale.getDefault())
                localTimeFormat.timeZone = TimeZone.getDefault()
                val time = localTimeFormat.parse(utcToLocal)!!.time
                val currentTime = System.currentTimeMillis()
                val difference = currentTime - time
                return if (difference > 0) MIN_30_IN_MILLI - difference else -1
            } catch (e: Exception) {
                -1
            }
        }

        fun milliToMin(milli: Long): String {
            var sec = milli.div(1000)
            val min = sec.div(60)
            sec = sec.rem(60)

            val secFixed = String.format("%02d", sec)
            val minFixed = String.format("%02d", min)
            return "$minFixed:$secFixed"
        }
    }

    fun jsonFromShippingAddress(obj: ShippingAddress): String {
        return Gson().toJson(obj)
    }

    fun jsonToShippingAddress(json: String): ShippingAddress {
        return Gson().fromJson(json, ShippingAddress::class.java)
    }

    fun jsonFromWallet(obj: Wallet): String {
        return Gson().toJson(obj)
    }

    fun jsonToWallet(json: String): Wallet {
        return Gson().fromJson(json, Wallet::class.java)
    }

    fun jsonFromPaymentMethod(obj: PaymentMethod): String {
        return Gson().toJson(obj)
    }

    fun jsonToPaymentMethod(json: String): PaymentMethod {
        return Gson().fromJson(json, PaymentMethod::class.java)
    }

    fun openLink(context: Context, link: String) {
        var url = link
        if (!url.startsWith("www.") && !url.startsWith("http")) url = "www.$url"
        if (!url.startsWith("http")) url = "https://$url"

        val uri = Uri.parse(url)
        openUri(context, uri)
    }

    fun openUri(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}