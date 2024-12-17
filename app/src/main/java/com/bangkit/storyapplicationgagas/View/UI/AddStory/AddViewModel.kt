package com.bangkit.storyapplicationgagas.View.UI.AddStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.FileUploadResponse
import com.bangkit.storyapplicationgagas.Data.Response.Response
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _postResult = MutableLiveData<Response<FileUploadResponse>>()
    val postResult: LiveData<Response<FileUploadResponse>> = _postResult

    fun postStory(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        viewModelScope.launch {
            _postResult.value = Response.Loading
            try {
                storyRepository.postStory(photo, description, lat, lon).collect { result ->
                    _postResult.value = result
                }
            } catch (e: Exception) {
                _postResult.value = Response.Error(e.message.toString())
            }
        }
    }

}

