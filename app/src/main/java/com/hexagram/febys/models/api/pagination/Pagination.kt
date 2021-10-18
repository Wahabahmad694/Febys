package com.hexagram.febys.models.api.pagination

import com.google.gson.Gson
import com.google.gson.JsonObject

data class Pagination(
    val listing: JsonObject
){
    inline fun <reified T> getResponse(): T = Gson().fromJson(listing, T::class.java)
}
