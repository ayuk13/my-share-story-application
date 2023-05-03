package com.dicoding.mysharestory.ui.primary

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.mysharestory.model.LoginRequest
import com.dicoding.mysharestory.model.RegisterRequest
import kotlinx.coroutines.launch
import com.dicoding.mysharestory.repository.TokenPreferences
import com.dicoding.mysharestory.repository.api.ApiConfig
import com.dicoding.mysharestory.repository.api.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

class PrimaryViewModel(application: Application) : AndroidViewModel(application) {

    private val keyLoginToken = stringPreferencesKey("login_token")
    private val nameKey = stringPreferencesKey("name")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "login_token_setting"
    )
    private val pref = TokenPreferences.getInstance(application.dataStore)

    private val service = ApiConfig.getApiService()

    private var _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> get()= _loadingStatus

    private var _registerUserStatus = MutableLiveData<Boolean>()
    val registerUserStatus: LiveData<Boolean> = _registerUserStatus

    private var _duplicateEmailStatus = MutableLiveData<Boolean>()
    val duplicateEmailStatus: LiveData<Boolean> = _duplicateEmailStatus

    private var _loginUserStatus = MutableLiveData<Boolean>()
    val loginUserStatus: LiveData<Boolean> = _loginUserStatus

    suspend fun registerUser(
        requestBody: RegisterRequest,
        dispatch: CoroutineDispatcher = Dispatchers.IO,
        apiService: ApiService = service) {

        _loadingStatus.postValue(true)
        try {
            val response = withContext(dispatch) {
                apiService.registerUser(requestBody)
            }

            when (response.error as Boolean) {
                true -> {
                    _loadingStatus.postValue(false)
                    _registerUserStatus.postValue(false)

                }
                false -> {
                    _registerUserStatus.postValue(true)
                    loginUser(
                        LoginRequest(requestBody.email, requestBody.password),
                        dispatch,
                        apiService
                    )
                }
            }
        } catch (e: UnknownHostException) {
            _loadingStatus.postValue(false)
            _registerUserStatus.postValue(false)
        } catch (e: Exception) {
            _loadingStatus.postValue(false)
            _duplicateEmailStatus.postValue(true)
        }
    }

    suspend fun loginUser(
        requestBody: LoginRequest,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiService: ApiService = service
    ){
        _loadingStatus.value = true
        try {
            val response = withContext(dispatcher) {
                apiService.loginUser(requestBody)
            }

            when (response.error as Boolean) {
                true -> {
                    _loadingStatus.value = false
                    _loginUserStatus.value = false
                }
                false -> {
                    _loadingStatus.value = false
                    _loginUserStatus.value = true

                    viewModelScope.launch(dispatcher) {
                        pref.saveUserPreference(
                            response.loginResult?.token ?: "",
                            keyLoginToken
                        )
                        pref.saveUserPreference(
                            response.loginResult?.name ?: "",
                            nameKey
                        )
                    }
                }
            }
        } catch (e: UnknownHostException) {
            _loadingStatus.value = false
            _loginUserStatus.value = false
        } catch (e: java.lang.Exception) {
            _loadingStatus.value = false
            _loginUserStatus.value = false
        }
    }
}