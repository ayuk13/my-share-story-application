package com.dicoding.mysharestory.ui.maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.mysharestory.model.Story
import com.dicoding.mysharestory.repository.db.StoryDao
import com.dicoding.mysharestory.repository.db.StoryDatabase
import kotlinx.coroutines.async

class MapsViewModel (application: Application) : AndroidViewModel(application) {
    private val storyDao = StoryDatabase(getApplication()).storiesDao()

    private var _storyList = MutableLiveData<List<Story>>()
    val storyList: LiveData<List<Story>> = _storyList

    suspend fun getMapStoryList(dao: StoryDao = storyDao) {
        val story = viewModelScope.async {
            dao.getMapStories()
        }

        _storyList.value = story.await()
    }
}