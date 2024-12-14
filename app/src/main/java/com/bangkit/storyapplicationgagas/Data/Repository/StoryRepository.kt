package com.bangkit.storyapplicationgagas.Data.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.storyapplicationgagas.Data.Config.ApiConfig
import com.bangkit.storyapplicationgagas.Data.Preference.UserPreference
import com.bangkit.storyapplicationgagas.Data.Response.FileUploadResponse
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.Data.Response.Story
import com.bangkit.storyapplicationgagas.Data.Service.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository private constructor(private val apiService: ApiService, private val userPreference: UserPreference,) {

    fun StoryList(): LiveData<List<ListStoryItem>> = liveData {
        emit(emptyList())
        val token = userPreference.getToken()
        val apiService = ApiConfig.getApiService(token.toString())
        try {
            val response = apiService.getStories()
            val stories = response.listStory
            if (!response.error) {
                emit(stories)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun detailStory(id: String): Flow<Response<Story>> = flow {
        emit(Response.Loading)
        val token = userPreference.getToken()
        val apiService = ApiConfig.getApiService(token.toString())
        try {
            val response = apiService.getDetailStory(id)
            val story = response.story
            emit(Response.Success(story))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    fun userStory(name: String): LiveData<Response<List<ListStoryItem>>> = liveData {
        emit(Response.Loading)
        val token = userPreference.getToken()
        val apiService = ApiConfig.getApiService(token.toString())
        try {
            val response = apiService.getStories()
            val result = response.listStory

            val filterName = result.filter { story ->
                story.name == name
            }

            emit(Response.Success(filterName))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun postStory(multipartBody: MultipartBody.Part, description: RequestBody): Flow<Response<FileUploadResponse>> = flow {
        emit(Response.Loading)
        val token = userPreference.getToken()
        val apiService = ApiConfig.getApiService(token.toString())
        try {
            val response = apiService.postStory(multipartBody, description)
            emit(Response.Success(response))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An error occurred"))
        }
    }


    suspend fun getStoriesWithLocation(): List<Story> {
        // Dapatkan token dari UserPreference
        val token = userPreference.getToken()

        // Pastikan token tidak null. Jika null, gunakan string kosong atau token default lainnya.
        val validToken = token ?: ""  // Token kosong jika null

        // Pastikan validToken tidak null saat dipassing ke ApiService
        val apiService = ApiConfig.getApiService(validToken)

        try {
            // Memanggil API untuk mendapatkan stories
            val response = apiService.getStoriesWithLocation(location = 1)  // Menambahkan lokasi = 1
            return response.listStory.map { listStoryItem ->
                // Map ListStoryItem ke Story jika diperlukan
                Story(
                    id = listStoryItem.id,
                    name = listStoryItem.name,
                    description = listStoryItem.description.toString(),
                    photoUrl = listStoryItem.photoUrl,
                    createdAt = listStoryItem.createdAt,
                    lat = listStoryItem.lat,
                    lon = listStoryItem.lon
                )
            }
        } catch (e: Exception) {
            return emptyList()  // Menangani error dengan mengembalikan list kosong
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ) = instance ?: synchronized(this) {
            instance ?: StoryRepository(apiService, userPreference)
        }.also { instance = it }
    }
}