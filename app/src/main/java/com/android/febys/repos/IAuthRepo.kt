package com.android.febys.repos

import com.android.febys.dto.UserDTO
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseSignup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IAuthRepo {
    fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseSignup>>

    suspend fun saveUser(userDTO: UserDTO)
}