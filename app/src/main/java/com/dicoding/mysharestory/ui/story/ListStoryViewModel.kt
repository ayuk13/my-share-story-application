package com.dicoding.mysharestory.ui.story

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mysharestory.model.Story
import com.dicoding.mysharestory.repository.StoriesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.dicoding.mysharestory.repository.TokenPreferences
import com.dicoding.mysharestory.repository.api.ApiConfig
import com.dicoding.mysharestory.repository.api.ApiService
import com.dicoding.mysharestory.utils.Event
import com.dicoding.mysharestory.utils.Utils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.UnknownHostException

class ListStoryViewModel(
    private val storiesRepository: StoriesRepository,
    application: Application
) : AndroidViewModel(application) {

    private val keyLoginToken = stringPreferencesKey("login_token")
    private val nameKey = stringPreferencesKey("name")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "login_token_setting"
    )
    private val pref = TokenPreferences.getInstance(application.dataStore)

    private val service = ApiConfig.getApiService()

    private var _loadingStatus = MutableLiveData<Boolean>()
    val loadingStatus: LiveData<Boolean> = _loadingStatus

    private var _successStatus = MutableLiveData<Event<Boolean>>()
    val successStatus: LiveData<Event<Boolean>> = _successStatus

    private var _onCleared = MutableLiveData<Boolean>()
    val onCleared: LiveData<Boolean> = _onCleared

    @JvmName("getToken1")
    fun getTokenUser(): LiveData<String> {
        return pref.loadUserPreference(keyLoginToken).asLiveData()
    }

    fun getNameUser(): LiveData<String> {
        return pref.loadUserPreference(nameKey).asLiveData()
    }

    fun clearTokenUser() {
        viewModelScope.launch {
            pref.clearUserPreference(keyLoginToken)
            pref.clearUserPreference(nameKey)
            _onCleared.value = true
        }
    }


    fun getListStory(token:String): LiveData<PagingData<Story>> = storiesRepository.getStory(
        token
    ).cachedIn(viewModelScope)

    fun uploadStory(
        token: String,
        fileImage: File,
        imageDescription: String,
        lat: String = "",
        lng: String = "",
        dispatch: CoroutineDispatcher = Dispatchers.IO,
        apiService: ApiService = service,
        utils: Utils = Utils
    ) {
        _loadingStatus.value = true

        viewModelScope.launch(dispatch) {

            val description = imageDescription.toRequestBody(
                "text/plain".toMediaType()
            )
            val requestLat = lat.toRequestBody(
                "text/plain".toMediaType()
            )
            val requestLng = lng.toRequestBody(
                "text/plain".toMediaType()
            )
            val requestImageFile = utils.reduceFileImage(fileImage).asRequestBody(
                "image/jpeg".toMediaTypeOrNull()
            )
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                fileImage.name,
                requestImageFile
            )

            try {
                val response = withContext(dispatch) {
                    apiService.uploadStory(
                        "Bearer $token",
                        imageMultipart,
                        description,
                        requestLat,
                        requestLng
                    )
                }

                when (response.error as Boolean) {
                    false -> {
                        _loadingStatus.postValue(false)
                        _successStatus.postValue(Event(true))
                    }

                    true -> {
                        _loadingStatus.postValue(false)
                        _successStatus.postValue(Event(false))
                    }
                }
            } catch (e: UnknownHostException) {
                _loadingStatus.postValue(false)
                _successStatus.postValue(Event(false))
            } catch (e: Exception) {
                _loadingStatus.postValue(false)
                _successStatus.postValue(Event(false))
            }
        }
    }

}