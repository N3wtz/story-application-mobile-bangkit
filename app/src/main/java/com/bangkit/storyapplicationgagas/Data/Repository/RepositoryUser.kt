package com.bangkit.storyapplicationgagas.Data.Repository

import android.util.Log
import com.bangkit.storyapplicationgagas.Data.Preference.Model
import com.bangkit.storyapplicationgagas.Data.Preference.UserPreference
import com.bangkit.storyapplicationgagas.Data.Response.LoginResponse
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.Data.Response.UserResponse
import com.bangkit.storyapplicationgagas.Data.Service.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class RepositoryUser private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun saveSession(user: Model) {
        userPreference.saveSession(user)
    }



    fun getSession(): Flow<Model> {
        return userPreference.getSession()
    }


    suspend fun register(name: String, email: String, password: String): Flow<Response<UserResponse>> = flow {
        emit(Response.Loading)
        try {

            val response = apiService.register(name, email, password)
            emit(Response.Success(response))
        } catch (t: Throwable) {
            when (t) {
                is HttpException -> {
                    try {
                        val errorResponse = Gson().fromJson(
                            t.response()?.errorBody()?.charStream(),
                            UserResponse::class.java
                        )
                        Log.e("TAG_LOGIN", "onFailure: ${errorResponse.message}")
                        emit(Response.Error(errorResponse.message))
                    } catch (e: Exception) {
                        Log.e("TAG_LOGIN", "onFailure: ${e.message.toString()}")
                        emit(Response.Error(e.message.toString()))
                    }
                }

                else -> {
                    Log.e("TAG_LOGIN", "onFailure: ${t.message.toString()}")
                    emit(Response.Error(t.message.toString()))
                }
            }
        }
    }

    suspend fun login(email: String, password: String): Flow<Response<LoginResponse>> = flow {
        emit(Response.Loading)
        try {
            val response = apiService.login(email, password)
            userPreference.saveSession(Model(response.loginResult.name,response.loginResult.token))
            emit(Response.Success(response))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: RepositoryUser? = null
        fun getInstance(
            apiService: ApiService ,
            userPreference: UserPreference
        ): RepositoryUser =
            instance ?: synchronized(this) {
                instance ?: RepositoryUser(apiService, userPreference)
            }.also { instance = it }
    }
}