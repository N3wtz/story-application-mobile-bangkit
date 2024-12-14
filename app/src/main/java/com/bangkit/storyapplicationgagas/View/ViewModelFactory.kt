package com.bangkit.storyapplicationgagas.View

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapplicationgagas.DI.Injection
import com.bangkit.storyapplicationgagas.Data.Repository.RepositoryUser
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.View.UI.AddStory.AddViewModel
import com.bangkit.storyapplicationgagas.View.UI.List.StoryViewModel
import com.bangkit.storyapplicationgagas.View.UI.Login.LoginViewModel
import com.bangkit.storyapplicationgagas.View.UI.MainViewModel
import com.bangkit.storyapplicationgagas.View.UI.Maps.MapViewModel
import com.bangkit.storyapplicationgagas.View.UI.SignUp.SignUpViewModel
import com.bangkit.storyapplicationgagas.View.UI.User.UserViewModel

class ViewModelFactory(
    private val repository: RepositoryUser,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Log untuk debug ke ViewModel yang sedang dibuat
        println("Creating ViewModel: ${modelClass.simpleName}")

        return when {
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {  // Menambahkan MapViewModel
                MapViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.storyRepository(context)
                )
            }.also { INSTANCE = it }
    }
}