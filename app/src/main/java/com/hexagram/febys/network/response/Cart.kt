package com.hexagram.febys.network.response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class Cart(
    @SerializedName("cartInfo")
    val info: CartInfo
)
