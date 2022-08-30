package com.hexagram.febys.models.swoove

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val email: String?,
    val mobile: String?,
    val name: String?
):Parcelable