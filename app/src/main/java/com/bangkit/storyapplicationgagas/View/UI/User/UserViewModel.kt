package com.bangkit.storyapplicationgagas.View.UI.User

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.Data.Response.ListStoryItem
import com.bangkit.storyapplicationgagas.Data.Response.Response

class UserViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getMyStory(name: String): LiveData<Response<List<ListStoryItem>>> {
        // Memastikan nama tidak kosong sebelum memanggil repository
        return if (name.isNotBlank()) {
            repository.userStory(name)
        } else {
            // Mengembalikan LiveData kosong atau error jika nama tidak valid
            MutableLiveData(Response.Error("Username cannot be empty"))
        }
    }
}