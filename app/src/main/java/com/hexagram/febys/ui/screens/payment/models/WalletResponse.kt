package com.hexagram.febys.ui.screens.payment.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WalletResponse(
    val wallet: Wallet
): Parcelable
