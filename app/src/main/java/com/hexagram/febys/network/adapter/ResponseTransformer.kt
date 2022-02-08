package com.hexagram.febys.network.adapter

import java.io.IOException

/**
 * A suspend function for handling success response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onSuccess(
    onResult: suspend ApiResponse.ApiSuccessResponse<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiSuccessResponse) {
        onResult(this)
    }
    return this
}

/**
 * A suspend function for handling error response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onError(
    onResult: suspend ApiResponse.ApiFailureResponse.Error<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiFailureResponse.Error) {
        onResult(this)
    }
    return this
}

/**
 * A suspend function for handling exception response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.onException(
    onResult: suspend ApiResponse.ApiFailureResponse.Exception<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiFailureResponse.Exception && exception !is IOException) {
        onResult(this)
    }
    return this
}

@SuspensionFunction
suspend fun <T> ApiResponse<T>.onNetworkError(
    onResult: suspend ApiResponse.ApiFailureResponse.Exception<T>.() -> Unit
): ApiResponse<T> {
    if (this is ApiResponse.ApiFailureResponse.Exception && exception is IOException) {
        onResult(this)
    }
    return this
}

/** A message from the [ApiResponse.ApiFailureResponse.Error]. */
fun <T> ApiResponse.ApiFailureResponse.Error<T>.message(): String = toString()

/** A message from the [ApiResponse.ApiFailureResponse.Exception]. */
fun <T> ApiResponse.ApiFailureResponse.Exception<T>.message(): String = toString()
