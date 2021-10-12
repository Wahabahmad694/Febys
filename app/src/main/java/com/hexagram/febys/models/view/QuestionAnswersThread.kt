package com.hexagram.febys.models.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionAnswersThread constructor(
    val id: String,
    val question: Thread,
    val answers: MutableList<Thread>,
    val upVotes: MutableSet<String>,
    val downVotes: MutableSet<String>
) : Parcelable

@Parcelize
data class Thread constructor(
    val id: String,
    val senderId: String,
    val senderName: String,
    var message: String,
    val userType: String,
    val timeStamp: String
) : Parcelable