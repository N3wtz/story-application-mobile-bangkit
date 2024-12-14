package com.bangkit.storyapplicationgagas.Data.Service

import com.bangkit.storyapplicationgagas.Data.Response.DetailStoryResponse
import com.bangkit.storyapplicationgagas.Data.Response.FileUploadResponse
import com.bangkit.storyapplicationgagas.Data.Response.LoginResponse
import com.bangkit.storyapplicationgagas.Data.Response.StoryResponse
import com.bangkit.storyapplicationgagas.Data.Response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") string: String
    ): UserResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") string: String
    ): LoginResponse
    //
    @GET("stories")
    suspend fun getStories(
        @Query("location") location: Int = 0,  // Setel location default ke 0 untuk mengabaikan lokasi
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse


    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,  // Menambahkan query parameter location
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse



}