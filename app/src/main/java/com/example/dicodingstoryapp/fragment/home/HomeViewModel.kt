package com.example.dicodingstoryapp.fragment.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicodingstoryapp.data.api.model.ListStoryItem
import com.example.dicodingstoryapp.repository.UsersRepository


class HomeViewModel(usersRepository: UsersRepository, token: String) :
    ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> =
        usersRepository.getStory(token).cachedIn(viewModelScope)

}