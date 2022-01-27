package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attr(
    val _id: String,
    val name: String,
    val value: String,
    val values: List<String>
) : Parcelable {

    fun deepCopy(): Attr {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, Attr::class.java)
    }
}