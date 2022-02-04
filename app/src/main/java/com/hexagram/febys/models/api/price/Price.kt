package com.hexagram.febys.models.api.price

import android.icu.text.CompactDecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.icu.util.CurrencyAmount
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Price(
    val _id: String,
    val value: Double,
    val currency: String,
) : Parcelable {
    fun getFormattedPrice(): String {
        return formatPrice(value)
    }

    fun getFormattedPrice(multiplyBy: Int): String {
        return formatPrice(value * multiplyBy)
    }

    private fun formatPrice(value: Double): String {
        if (currency.isEmpty()) return value.toString()

        val format: NumberFormat = if (value < 1000) NumberFormat.getCurrencyInstance()
        else CompactDecimalFormat
            .getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
        format.maximumFractionDigits = 2
        val currency = Currency.getInstance(currency)
        val currencyAmount = CurrencyAmount(value, currency)
        return format.format(currencyAmount)
    }
}