package com.dicoding.mysharestory.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.mysharestory.model.Story
import com.dicoding.mysharestory.model.StoryRemoteMediator
import com.dicoding.mysharestory.repository.api.ApiService
import com.dicoding.mysharestory.repository.db.StoryDatabase

class StoriesRepository(
    private val database: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(apiKey:String): LiveData<PagingData<Story>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(apiService, database, apiKey),
            pagingSourceFactory = {
                database.storiesDao().getUserStories()
            }
        ).liveData
    }

}