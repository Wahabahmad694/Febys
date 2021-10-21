package com.hexagram.febys.models.api.role

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    val id: String,
    val name: String
) : Parcelable
