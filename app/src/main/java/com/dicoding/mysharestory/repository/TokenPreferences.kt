package com.dicoding.mysharestory.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenPreferences private constructor(
    private val dataStore: DataStore<Preferences>) {

    fun loadUserPreference(key: Preferences.Key<String>): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }
    }

    suspend fun saveUserPreference(token: String, key: Preferences.Key<String>) {
        dataStore.edit { preferences ->
            preferences[key] = token
        }
    }

    suspend fun clearUserPreference(key: Preferences.Key<String>) {
        dataStore.edit { preferences ->
            preferences[key] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TokenPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): TokenPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}