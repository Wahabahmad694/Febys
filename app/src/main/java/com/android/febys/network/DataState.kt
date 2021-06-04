package com.android.febys.network

import androidx.annotation.StringRes

sealed class DataState<T> {
    class Loading<T> : DataState<T>()
    data class Error<T>(val errorMessage: ErrorMessage) : DataState<T>()
    data class Data<T>(val data: T) : DataState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> error(error: String) = Error<T>(ErrorMessage.ErrorString(error))
        fun <T> error(@StringRes resId: Int) = Error<T>(ErrorMessage.ErrorRes(resId))
        fun <T> data(data: T) = Data(data)
    }
}

sealed class ErrorMessage {
    class ErrorRes(@StringRes val resId: Int) : ErrorMessage()
    class ErrorString(val message: String) : ErrorMessage()
}