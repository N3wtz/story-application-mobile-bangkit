package com.bangkit.storyapplicationgagas.View.UI

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.bangkit.storyapplicationgagas.Data.Preference.Model
import com.bangkit.storyapplicationgagas.Data.Repository.RepositoryUser

class MainViewModel(private val repository: RepositoryUser) : ViewModel() {

    // Mendapatkan data session
    fun getSession(): LiveData<Model> {
        return repository.getSession().asLiveData()
    }

    // Fungsi tambahan untuk memeriksa status session
    fun isUserLoggedIn(): LiveData<Boolean> {
        return getSession().map { session ->
            session != null // Memeriksa apakah session ada
        }
    }
}