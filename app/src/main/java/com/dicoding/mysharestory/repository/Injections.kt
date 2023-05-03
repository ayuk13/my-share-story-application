package com.dicoding.mysharestory.repository

import android.content.Context
import com.dicoding.mysharestory.repository.api.ApiConfig
import com.dicoding.mysharestory.repository.db.StoryDatabase

object Injections {
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoryDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoriesRepository(database, apiService)
    }
}