package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.chat.Chat
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionAnswers(
    val _id: String,
    val chat: MutableList<Chat>,
    @SerializedName("up_votes")
    val upVotes: MutableSet<String>,
    @SerializedName("down_votes")
    val downVotes: MutableSet<String>,
) : Parcelable