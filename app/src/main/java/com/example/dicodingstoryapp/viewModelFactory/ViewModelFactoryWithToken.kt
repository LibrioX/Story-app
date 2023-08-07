package com.example.dicodingstoryapp.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.di.Injection
import com.example.dicodingstoryapp.fragment.home.HomeViewModel
import com.example.dicodingstoryapp.repository.UsersRepository

class ViewModelFactoryWithToken private constructor(
    private val usersRepository: UsersRepository,
    private val token: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return token?.let { HomeViewModel(usersRepository, it) } as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactoryWithToken? = null
        fun getInstance(context: Context, token: String? = null): ViewModelFactoryWithToken =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactoryWithToken(Injection.provideRepository(context), token)
            }.also { instance = it }
    }

}