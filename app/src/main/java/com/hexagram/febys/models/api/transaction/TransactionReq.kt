package com.hexagram.febys.models.api.transaction

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionReq(
    val transaction_ids: List<String>
) : Parcelable
