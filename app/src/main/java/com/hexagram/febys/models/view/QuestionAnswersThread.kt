package com.hexagram.febys.models.view

data class QuestionAnswersThread(
    val id: String,
    val question: Thread,
    val answers: MutableList<Thread>,
    val upVotes: MutableSet<String>,
    val downVotes: MutableSet<String>
)

data class Thread(
    val id: String,
    val senderId: String,
    val senderName: String,
    var message: String,
    val userType: String,
    val timeStamp: String
)