package com.hexagram.febys.network

import com.hexagram.febys.R
import com.hexagram.febys.models.api.chat.Chat
import com.hexagram.febys.models.api.chat.Sender
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.view.*
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {

    private val questionAnswersThread = mutableListOf(
        QuestionAnswersThread(
            "1",
            Thread(
                "1",
                "1",
                "Houd",
                "What is quality of this product",
                "Consumer",
                "2021-10-04T20:39:02.785Z"
            ),
            mutableListOf(
                Thread(
                    "2",
                    "2",
                    "Daniyal",
                    "Quality is A+",
                    "Vendor",
                    "2021-10-04T20:45:02.785Z"
                ),
                Thread(
                    "3",
                    "1",
                    "Houd",
                    "Thank you",
                    "Consumer",
                    "2021-10-04T20:48:02.785Z"
                ),
            ),
            mutableSetOf("1", "2"),
            mutableSetOf("3", "4")
        ),
        QuestionAnswersThread(
            "2",
            Thread(
                "3",
                "1",
                "Houd",
                "What is quality of this product",
                "Consumer",
                "2021-10-04T21:39:02.785Z"
            ),
            mutableListOf(
                Thread(
                    "4",
                    "2",
                    "Daniyal",
                    "Quality is A+",
                    "Vendor",
                    "2021-10-04T22:45:02.785Z"
                ),
                Thread(
                    "5",
                    "1",
                    "Houd",
                    "Thank you",
                    "Consumer",
                    "2021-10-04T23:48:02.785Z"
                ),
            ),
            mutableSetOf("1", "2"),
            mutableSetOf("3", "4")
        )
    )

    fun fetchQuestionAnswersThread(): MutableList<QuestionAnswersThread> {
        return questionAnswersThread
    }

    suspend fun postQuestion(
        authToken: String, productId: String, msg: String
    ): ApiResponse.ApiSuccessResponse<QuestionAnswers> {
        delay(100)
        val question = QuestionAnswers(
            "" + questionAnswersThread.size + 1,
            mutableListOf(
                Chat(
                    "" + questionAnswersThread.size + 100,
                    Sender("1", "Houd"),
                    msg,
                    "Consumer",
                    "2021-11-04T20:39:02.785Z"
                )
            ), // answer
            mutableSetOf(), // up vote
            mutableSetOf()  // down vote
        )
//        questionAnswersThread.add(question)
        return ApiResponse.ApiSuccessResponse(Response.success(question))
    }

    suspend fun editQuestion(threadId: String, question: String): ApiResponse<Unit> {
        delay(100)
        val thread = questionAnswersThread.firstOrNull { it.id == threadId }
        thread?.question?.message = question
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun removeQuestion(threadId: String, question: String): ApiResponse<Unit> {
        delay(100)
        val index = questionAnswersThread.indexOfFirst { it.id == threadId }
        if (index != -1) questionAnswersThread.removeAt(index)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun postAnswer(answer: Thread, threadId: String): ApiResponse<Unit> {
        delay(100)
        questionAnswersThread.firstOrNull { it.id == threadId }?.answers?.add(answer)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun upVote(threadId: String, userId: String): ApiResponse<Unit> {
        delay(100)
        val thread = questionAnswersThread.firstOrNull { it.id == threadId }
        thread?.upVotes?.add(userId)
        thread?.downVotes?.remove(userId)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun downVote(threadId: String, userId: String): ApiResponse<Unit> {
        delay(100)
        val thread = questionAnswersThread.firstOrNull { it.id == threadId }
        thread?.downVotes?.add(userId)
        thread?.upVotes?.remove(userId)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    private val paymentMethods = mutableListOf(
        PaymentMethod(1, "RavePay", R.drawable.ic_rave_pay, true),
        PaymentMethod(2, "Mobile Money", R.drawable.ic_mobile_money, false),
        PaymentMethod(3, "Paypal", R.drawable.ic_paypal, false),
        PaymentMethod(4, "Cash on delivery", R.drawable.ic_cash_on_delivery, false)
    )

    suspend fun fetchPaymentMethods(): ApiResponse<List<PaymentMethod>> {
        delay(100)
        return ApiResponse.ApiSuccessResponse(Response.success(paymentMethods))
    }

    suspend fun setAsDefaultPaymentMethod(id: Int): PaymentMethod? {
        delay(100)
        paymentMethods.forEach { it.isDefault = false }
        val paymentMethod = paymentMethods.firstOrNull { it.id == id }
        paymentMethod?.isDefault = true
        return paymentMethod
    }
}