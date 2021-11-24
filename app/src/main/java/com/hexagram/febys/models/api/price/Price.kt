package com.hexagram.febys.models.api.price

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.NumberFormat
import java.util.*

@Parcelize
data class Price(
    val _id: String,
    val value: Double,
    val currency: String
) : Parcelable {
    fun getFormattedPrice(): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(currency)
        return format.format(value)
    }

    fun getFormattedPrice(multiplyBy: Int): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(currency)
        return format.format(value * multiplyBy)
    }

    fun getFormattedPriceByLocale(): String {
        val format = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("GHS")
        return format.format(value)
    }
}