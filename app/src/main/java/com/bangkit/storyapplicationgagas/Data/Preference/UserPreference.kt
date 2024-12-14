package com.bangkit.storyapplicationgagas.Data.Preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "register")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: Model) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = user.username
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<Model> {
        return dataStore.data.map { preferences ->
            Model(
                preferences[USERNAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.firstOrNull()
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>):UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}