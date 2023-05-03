package com.dicoding.mysharestory.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.dicoding.mysharestory.repository.TokenPreferences

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val keyLoginToken = stringPreferencesKey("login_token")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "login_token_setting"
    )
    private val pref = TokenPreferences.getInstance(application.dataStore)

    @JvmName("getToken1")
    fun getTokenUser(): LiveData<String>{
        return pref.loadUserPreference(keyLoginToken).asLiveData()
    }
}