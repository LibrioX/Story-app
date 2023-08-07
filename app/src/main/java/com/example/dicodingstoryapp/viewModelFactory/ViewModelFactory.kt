package com.example.dicodingstoryapp.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingstoryapp.di.Injection
import com.example.dicodingstoryapp.fragment.showStory.ShowStoryViewModel
import com.example.dicodingstoryapp.fragment.addStory.AddStoryViewModel
import com.example.dicodingstoryapp.fragment.home.HomeViewModel
import com.example.dicodingstoryapp.fragment.login.LoginViewModel
import com.example.dicodingstoryapp.fragment.mapsStory.MapsViewModel
import com.example.dicodingstoryapp.fragment.register.RegisterViewModel
import com.example.dicodingstoryapp.repository.UsersRepository


class ViewModelFactory private constructor(
    private val usersRepository: UsersRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(usersRepository, token = "") as T
        } else if (modelClass.isAssignableFrom(ShowStoryViewModel::class.java)) {
            return ShowStoryViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(usersRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(usersRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}