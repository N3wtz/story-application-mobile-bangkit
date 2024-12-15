package com.bangkit.storyapplicationgagas.View.UI.List

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.Data.Response.Story
import kotlinx.coroutines.launch

class StoryViewModel (private val repository : StoryRepository) : ViewModel () {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return repository.getStoriesPaging().cachedIn(viewModelScope).asLiveData()
    }

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