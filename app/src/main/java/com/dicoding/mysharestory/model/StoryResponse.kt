package com.dicoding.mysharestory.model

data class StoryResponse(
    val error: Boolean?,
    val listStory: List<Story>?,
    val loginResult: LoginResult?,
    val message: String?
)