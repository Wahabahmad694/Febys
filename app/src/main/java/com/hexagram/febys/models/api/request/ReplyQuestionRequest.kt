package com.hexagram.febys.models.api.request

import com.google.gson.annotations.SerializedName

data class ReplyQuestionRequest(
    @SerializedName("_id")
    private val threadId: String,
    private val message: String
)
