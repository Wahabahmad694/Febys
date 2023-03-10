package com.hexagram.febys.network

import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.vouchers.Voucher

sealed class DataState<T> {
    class Loading<T> : DataState<T>()

    data class Data<T>(val data: T) : DataState<T>()

    open class Error<T> : DataState<T>()
    data class ApiError<T>(val message: String) : DataState.Error<T>()
    class ExceptionError<T> : DataState.Error<T>()
    class NetworkError<T> : DataState.Error<T>()
}