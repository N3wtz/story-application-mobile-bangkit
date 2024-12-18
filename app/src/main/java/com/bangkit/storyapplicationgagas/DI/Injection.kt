package com.bangkit.storyapplicationgagas.DI

import android.content.Context
import com.bangkit.storyapplicationgagas.Data.Config.ApiConfig
import com.bangkit.storyapplicationgagas.Data.Preference.UserPreference
import com.bangkit.storyapplicationgagas.Data.Preference.dataStore
import com.bangkit.storyapplicationgagas.Data.Repository.RepositoryUser
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): RepositoryUser {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val userPreference = UserPreference.getInstance(context.dataStore)
        return RepositoryUser.getInstance(apiService, userPreference)
    }

    fun storyRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService, pref)
    }
}