package com.hexagram.febys.models.api.menu

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreTemplate(
    val _id: String,
    val active: Boolean,
    val content: String,
    val createdAt: String,
    val name: String,
    val updatedAt: String
) : Parcelable
