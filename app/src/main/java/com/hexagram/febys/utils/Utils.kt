package com.hexagram.febys.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
}