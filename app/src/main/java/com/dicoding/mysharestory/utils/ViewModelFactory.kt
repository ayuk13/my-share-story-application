package com.dicoding.mysharestory.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mysharestory.repository.Injections
import com.dicoding.mysharestory.repository.Injections.provideRepository
import com.dicoding.mysharestory.repository.StoriesRepository
import com.dicoding.mysharestory.repository.api.ApiConfig
import com.dicoding.mysharestory.repository.db.StoryDatabase
import com.dicoding.mysharestory.ui.story.ListStoryViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListStoryViewModel(
                provideRepository(context),
                context.applicationContext as Application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}