package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    val code: Int?,
    val message: String?,
    val errors: List<FieldError>
)

data class FieldError(
    @SerializedName("field")
    val field: String,
    @SerializedName("error")
    val error: String
)