package com.bangkit.storyapplicationgagas.View.UI.Maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.Story
import kotlinx.coroutines.launch

class MapViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> get() = _stories

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val response = repository.getStoriesWithLocation() // Ambil data lokasi dari API
                _stories.value = response
            } catch (e: Exception) {
                // Handle error jika ada
            }
        }
    }
}

