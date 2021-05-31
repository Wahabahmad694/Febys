package com.android.febys.utils

import androidx.annotation.StringRes
import com.android.febys.R
import retrofit2.HttpException
import java.net.SocketTimeoutException

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    data class Error<T> constructor(val errorMessage: ErrorMessage, val t: Throwable) : Resource<T>()
    data class Data<T>(val data: T) : Resource<T>()

    companion object {
        fun <T> getError(t: Throwable): Resource<T> {
            return when (t) {
                is SocketTimeoutException -> {
                    Error(ErrorMessage.ErrorRes(R.string.error_socket_timeout), t)
                }
                is HttpException -> {
                    if (t.code() in 400..499) {
                        Error(ErrorMessage.ErrorString(t.localizedMessage ?: t.message()), t)
                    } else {
                        Error(ErrorMessage.ErrorRes(R.string.error_unknown), t)
                    }
                }
                else -> {
                    Error(ErrorMessage.ErrorRes(R.string.error_unknown), t)
                }
            }
        }
    }
}

sealed class ErrorMessage {
    class ErrorRes(@StringRes val resId: Int) : ErrorMessage()
    class ErrorString(val message: String) : ErrorMessage()
}