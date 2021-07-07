package com.hexagram.febys.network.response

import com.google.gson.Gson
import com.google.gson.JsonObject

data class ResponseOfPagination(
    val listing: JsonObject
) {
    inline fun <reified T> getResponse(): T = Gson().fromJson(listing, T::class.java)
}
