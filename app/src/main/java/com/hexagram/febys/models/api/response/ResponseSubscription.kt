package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.hexagram.febys.models.api.subscription.Subscription
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseSubscription(
    val subscription: Subscription
) : Parcelable
