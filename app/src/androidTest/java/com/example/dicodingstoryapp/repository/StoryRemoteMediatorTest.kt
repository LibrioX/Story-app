package com.example.dicodingstoryapp.repository

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dicodingstoryapp.data.api.config.ApiService
import com.example.dicodingstoryapp.data.api.config.BasicResponse
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.data.api.model.ResponseStories
import com.example.dicodingstoryapp.data.api.model.ResponseStory
import com.example.dicodingstoryapp.data.api.model.ResponseUser
import com.example.dicodingstoryapp.data.local.StoryDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            "token"
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }


    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(name: String, email: String, password: String): BasicResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): ResponseUser {
        TODO("Not yet implemented")
    }

    override suspend fun postStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): BasicResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(
        token: String,
        page: Int,
        size: Int,
        location: Int?
    ): ResponseStories {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                 i.toDouble(),
                "$i",
                i.toDouble(),
            )
            items.add(story)
        }
        return ResponseStories(
            items.subList((page - 1) * size, (page - 1) * size + size),
            false,
            "Stories fetched successfully"
        )
    }

    override suspend fun getDetailStory(id: String, token: String): ResponseStory {
        TODO("Not yet implemented")
    }
}

