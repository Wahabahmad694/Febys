package com.hexagram.febys.ui.screens.payment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.utils.Utils
import com.hexagram.febys.utils.roundOffDecimal
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wallet(
    val _id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("currency")
    var _currency: String?,
    @SerializedName("available_balance")
    var availableBalance: Double,
    var conversionCurrency: String? = null,
    var conversionRate: Double? = null
) : Parcelable {

    val isWalletCreated get() = _currency != null

    val currency
        get() = conversionCurrency ?: _currency

    val amount
        get() = if (conversionRate != null) {
            availableBalance.times(conversionRate!!).roundOffDecimal()
        } else {
            availableBalance
        }

    fun getPrice(): Price {
        return Price("", amount, currency ?: Utils.DEFAULT_CURRENCY)
    }
}
