package com.bangkit.storyapplicationgagas.View.UI.SignUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapplicationgagas.Data.Repository.RepositoryUser
import com.bangkit.storyapplicationgagas.Data.Response.Response
import com.bangkit.storyapplicationgagas.Data.Response.UserResponse
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: RepositoryUser) : ViewModel() {

    // LiveData untuk perubahan pada status hasil registrasi
    private val _userResult = MutableLiveData<Response<UserResponse>>()
    val UserResult: LiveData<Response<UserResponse>> get() = _userResult

    // Fungsi untuk mendaftarkan user
    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            // Mengambil hasil dari repository dan memperbarui LiveData
            repository.register(name, email, password).collect { result ->
                _userResult.value = result
            }
        }
    }
}