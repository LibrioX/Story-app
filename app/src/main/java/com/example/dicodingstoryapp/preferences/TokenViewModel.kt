package com.example.dicodingstoryapp.preferences

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class TokenViewModel(private val pref: UserPreferences) : ViewModel() {

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun getLogin(): LiveData<Boolean> {
        return pref.getLogin().asLiveData()
    }

    fun saveLogin(hasLoggedIn: Boolean) {
        viewModelScope.launch {
            pref.saveLogin(hasLoggedIn)
        }
    }

    fun clearPreferences() {
        viewModelScope.launch {
            pref.clearPreferences()
        }
    }


}
