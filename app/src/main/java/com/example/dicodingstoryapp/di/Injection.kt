package com.example.dicodingstoryapp.di

import android.content.Context
import com.example.dicodingstoryapp.data.api.config.ApiConfig
import com.example.dicodingstoryapp.data.local.StoryDatabase
import com.example.dicodingstoryapp.repository.UsersRepository


object Injection {
    fun provideRepository(context: Context): UsersRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return UsersRepository.getInstance(database, apiService)
    }
}