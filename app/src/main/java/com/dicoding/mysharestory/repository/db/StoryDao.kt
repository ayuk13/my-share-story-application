package com.dicoding.mysharestory.repository.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.mysharestory.model.Story

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(stories: List<Story>)

    @Query("SELECT * FROM story")
    fun getUserStories(): PagingSource<Int, Story>

    @Query("SELECT * FROM story")
    suspend fun getMapStories(): List<Story>

    @Query("SELECT * FROM story WHERE id = :storyId")
    suspend fun getStory(storyId: String): Story?

    @Query("DELETE FROM story WHERE id = :storyId")
    suspend fun deleteStory(storyId: String)

    @Query("DELETE FROM story")
    suspend fun deleteStories()
}