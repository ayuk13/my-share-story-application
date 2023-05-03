package com.dicoding.mysharestory.repository.api

import com.dicoding.mysharestory.model.LoginRequest
import com.dicoding.mysharestory.model.RegisterRequest
import com.dicoding.mysharestory.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun registerUser(
        @Body requestBody: RegisterRequest
    ): StoryResponse

    @POST("login")
    suspend fun loginUser(
        @Body requestBody: LoginRequest
    ): StoryResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") authKey: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 10
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") authKey: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lng: RequestBody
    ): StoryResponse
}