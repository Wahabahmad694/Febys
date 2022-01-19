package com.hexagram.febys.repos

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.network.requests.RequestUpdateUser
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class ProfileRepoImp @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger
) : IProfileRepo {
    override suspend fun fetchProfile(dispatcher: CoroutineDispatcher) =
        flow<DataState<Consumer>> {
            val authToken = pref.getAccessToken()
            if (authToken.isEmpty()) return@flow
            val response = backendService.fetchProfile(authToken)
            response.onSuccess {
                emit(DataState.Data(data!!.consumerInfo))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)

    override suspend fun updateProfile(
        requestUpdateUser: RequestUpdateUser, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Consumer>> = flow<DataState<Consumer>> {
        val authToken = pref.getAccessToken()
        val response = backendService.updateProfile(authToken, requestUpdateUser)
        response
            .onSuccess {
                pref.saveConsumer(data!!.user)
                emit(DataState.Data(data.user))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun updateProfileImage(
        filePath: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<List<String>>> = flow<DataState<List<String>>> {
        val fileToUpload = File(filePath)
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "media",
            fileToUpload.getName(),
            RequestBody.create("image/*".toMediaTypeOrNull(), fileToUpload)
        )
        val authToken = pref.getAccessToken()
        val response = backendService.uploadImage(authToken, filePart)
        response
            .onSuccess {
                emit(DataState.Data(data!!))
                try {
                    fileToUpload.delete()
                }
                catch (e:Exception){

                }
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError {emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}