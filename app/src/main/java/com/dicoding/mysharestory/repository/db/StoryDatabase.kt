package com.dicoding.mysharestory.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.mysharestory.model.Story

@Database(
    entities = [
        Story::class,
        RemoteKeys::class
    ],
    version = 1
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storiesDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object{
        @Volatile
        private var instance: StoryDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java,
            "storydatabase"
        ).fallbackToDestructiveMigration().build()
    }
}