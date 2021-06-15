package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class FieldError(
    @SerializedName("field")
    val field: String,
    @SerializedName("error")
    val error: String
)