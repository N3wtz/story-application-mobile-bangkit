package com.bangkit.storyapplicationgagas.View.UI.List

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.Data.Response.Story
import kotlinx.coroutines.launch

class StoryViewModel (private val repository : StoryRepository) : ViewModel () {

    fun storyList(): LiveData<List<ListStoryItem>> = repository.StoryList()

    private val _detailResult = MutableLiveData<Response<Story>>()
    val detailResult: LiveData<Response<Story>> = _detailResult

    fun detailStory(id: String) {
        viewModelScope.launch {
            repository.detailStory(id).collect { result ->
                _detailResult.value = result
            }
        }
    }

}