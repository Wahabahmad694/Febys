package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.product.QAThread
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuestionAnswersResponse(
    val questionAnswers: QuestionAnswers
) : Parcelable

@Parcelize
data class QuestionAnswers(
    val _id: String,
    @SerializedName("product_id")
    val productId: String,
    private val threads: MutableList<QAThread>
) : Parcelable {
    val qaThreads
        get() = threads.asReversed()
}
