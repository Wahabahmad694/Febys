package com.hexagram.febys.models.api.subscription

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.transaction.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subscription(
    @SerializedName("_id")
    val id: String,
    @SerializedName("consumer_id")
    val consumerId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    val expiry: String,
    @SerializedName("package_info")
    val packageInfo: Package,
    val transactions: List<Transaction>,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable