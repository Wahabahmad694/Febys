package com.hexagram.febys.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hexagram.febys.models.view.ShippingAddress

object Utils {
    fun jsonToSetOfInt(json: String): MutableSet<Int> {
        return if (json.isEmpty()) {
            mutableSetOf()
        } else {
            val typeToken = object : TypeToken<MutableSet<Int>>() {}.type
            Gson().fromJson(json, typeToken)
        }
    }

    fun jsonFromSetOfInt(set: MutableSet<Int>): String {
        val typeToken = object : TypeToken<MutableSet<Int>>() {}.type
        return Gson().toJson(set, typeToken)
    }

    fun jsonFromShippingAddress(obj: ShippingAddress): String {
        return Gson().toJson(obj)
    }

    fun jsonToShippingAddress(json: String): ShippingAddress {
        return Gson().fromJson(json, ShippingAddress::class.java)
    }

    fun openLink(context: Context, link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}