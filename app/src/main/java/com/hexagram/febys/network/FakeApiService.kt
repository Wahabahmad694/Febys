package com.hexagram.febys.network

import com.hexagram.febys.R
import com.hexagram.febys.models.view.*
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {
    private val addresses = mutableListOf(
        ShippingAddress(
            1,
            "Hillary",
            "Widanama",
            "Home",
            "Ghana",
            "Virtual Incubator, Busy Internet",
            null,
            "Airport Res, Area",
            "Ghana",
            "233321",
            "03331234567",
            true
        ),
        ShippingAddress(
            2,
            "Hillary",
            "Widanama",
            "Office",
            "Ghana",
            "No. 18 Third Close, Airport Res Area",
            null,
            "Airport Res, Area",
            "Ghana",
            "233321",
            "03001234567",
            false
        )
    )

    suspend fun fetchShippingAddress(authToken: String): ApiResponse<List<ShippingAddress>> {
        delay(1000)
        return ApiResponse.ApiSuccessResponse(Response.success(addresses))
    }

    suspend fun setAsDefault(id: Int): ShippingAddress? {
        delay(100)
        addresses.forEach { it.isDefault = false }
        val shippingAddress = addresses.firstOrNull { it.id == id }
        shippingAddress?.isDefault = true
        return shippingAddress
    }

    suspend fun updateShippingAddress(
        authToken: String, shippingAddress: ShippingAddress
    ): ApiResponse<Unit> {
        delay(100)
        val index = addresses.indexOfFirst { it.id == shippingAddress.id }
        if (index != -1) {
            addresses.removeAt(index)
            addresses.add(index, shippingAddress)
            if (shippingAddress.isDefault) {
                setAsDefault(shippingAddress.id)
            }
        }
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun addShippingAddress(
        authToken: String, shippingAddress: ShippingAddress
    ): ApiResponse<Unit> {
        delay(100)
        addresses.add(shippingAddress)
        if (shippingAddress.isDefault) setAsDefault(shippingAddress.id)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun fetchVendorDetail(vendorId: Int): ApiResponse<VendorDetail> {
        delay(100)
        return ApiResponse.ApiSuccessResponse(Response.success(getVendorDetail()))
    }

    private fun getVendorDetail(): VendorDetail {
        return VendorDetail(
            23,
            "Gwyneth Paltrow",
            "Enter the romantic, eclectic and free-spirited world of Gucci at Febys.",
            "res:///${R.drawable.temp_celebrity_header}",
            "res:///${R.drawable.temp_celebrity_profile_img}",
            "Beauty Store",
            "Ground Floor, Senya Terazzo Building, Behind...",
            false,
            listOf(SocialLink(R.drawable.ic_social_link_fb, "https://www.facebook.com")),
            listOf(
                Endorsement(
                    1, "Edward A Kuffour",
                    "res:///${R.drawable.temp_celebrity_profile_img}"
                ),
                Endorsement(
                    2, "Edward A Kuffour",
                    "res:///${R.drawable.temp_celebrity_profile_img}"
                ),
                Endorsement(
                    3, "Edward A Kuffour",
                    "res:///${R.drawable.temp_celebrity_profile_img}"
                )
            )
        )
    }

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
        authToken: String, productId: Int, question: String
    ): ApiResponse.ApiSuccessResponse<QuestionAnswersThread> {
        delay(100)
        val question = QuestionAnswersThread(
            "" + questionAnswersThread.size + 1,
            Thread(
                (questionAnswersThread.size + 100).toString(),
                "1",
                "Houd",
                question,
                "Consumer",
                "2021-11-04T20:39:02.785Z"
            ),
            mutableListOf(), // answer
            mutableSetOf(), // up vote
            mutableSetOf()  // down vote
        )
        questionAnswersThread.add(question)
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
}