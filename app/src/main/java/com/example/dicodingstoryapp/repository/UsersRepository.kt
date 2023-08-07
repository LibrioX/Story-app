package com.example.dicodingstoryapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.dicodingstoryapp.data.api.config.ApiService
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.data.local.StoryDatabase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UsersRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val userApi: ApiService
) {

    suspend fun registerUser(username: String, email: String, password: String) =
        userApi.register(username, email, password)

    suspend fun loginUser(email: String, password: String) = userApi.login(email, password)

    suspend fun postStoriesWithLocation(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = userApi.postStories(token, file, description, lat, lon)

    suspend fun getStoriesWithLocation(token: String, page: Int, location: Int) =
        userApi.getStories(token, page, 20, location)

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, userApi, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getDetailStory(id: String, token: String) = userApi.getDetailStory(id, token)

    companion object {
        @Volatile
        private var instance: UsersRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
        ): UsersRepository =
            instance ?: synchronized(this) {
                instance ?: UsersRepository(storyDatabase, apiService)
            }.also { instance = it }
    }

}



