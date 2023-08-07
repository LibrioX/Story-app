package com.example.dicodingstoryapp.data.api.config


import com.example.dicodingstoryapp.data.api.model.ResponseStories
import com.example.dicodingstoryapp.data.api.model.ResponseStory
import com.example.dicodingstoryapp.data.api.model.ResponseUser
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

data class BasicResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

interface ApiService {

    @FormUrlEncoded
    @POST("v1/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): BasicResponse

    @FormUrlEncoded
    @POST("v1/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseUser

    @Multipart
    @POST("v1/stories")
    suspend fun postStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): BasicResponse

    @GET("v1/stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 15,
        @Query("location") location: Int? = 0
    ): ResponseStories


    @GET("v1/stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String,
        @Header("Authorization") token: String,
    ): ResponseStory

}