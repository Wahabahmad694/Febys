package com.android.febys.network

import androidx.annotation.StringRes

sealed class DataState<T> {
    class Loading<T> : DataState<T>()
    data class Error<T>(val errorMessage: ErrorMessage, val t: Throwable) : DataState<T>()
    data class Data<T>(val data: T) : DataState<T>()
}

sealed class ErrorMessage {
    class ErrorRes(@StringRes val resId: Int) : ErrorMessage()
    class ErrorString(val message: String) : ErrorMessage()
}