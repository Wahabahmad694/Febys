package com.hexagram.febys.paginations

interface PagingResponseCallback<in T> {
    fun onPagingResponse(response: T)
}