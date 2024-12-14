package com.bangkit.storyapplicationgagas.View.UI.Login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.storyapplicationgagas.Data.Preference.Model
import com.bangkit.storyapplicationgagas.Data.Repository.RepositoryUser
import com.bangkit.storyapplicationgagas.Data.Response.LoginResponse
import com.bangkit.storyapplicationgagas.Data.Response.Response
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: RepositoryUser) : ViewModel() {

    private val _loginResult = MutableLiveData<Response<LoginResponse>>()
    val loginResult: LiveData<Response<LoginResponse>> get() = _loginResult

    // Login autentikasi user
    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                _loginResult.value = result
            }
        }
    }

    // Menyimpan session data ke repository
    fun saveSession(user: Model) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    // Ambil session data dari repository
    fun getSession(): LiveData<Model> = repository.getSession().asLiveData()

    // Logout function untuk clear session
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}