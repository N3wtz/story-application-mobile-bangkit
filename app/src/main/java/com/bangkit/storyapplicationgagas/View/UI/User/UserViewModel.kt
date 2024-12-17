package com.bangkit.storyapplicationgagas.View.UI.User

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response

class UserViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getMyStory(name: String): LiveData<Response<List<ListStoryItem>>> {
        return if (name.isNotBlank()) {
            repository.userStory(name)
        } else {
            MutableLiveData(Response.Error("Username cannot be empty"))
        }
    }
}