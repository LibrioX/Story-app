package com.example.dicodingstoryapp.fragment.mapsStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.api.model.ResponseStories
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.repository.UsersRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class MapsViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _storiesResult = MutableLiveData<Result<ResponseStories>>()
    val storiesResult: LiveData<Result<ResponseStories>> = _storiesResult

    private val _token = MutableLiveData<String>()

    fun setToken(username: String) {
        _token.value = username
    }

    private fun getToken() = _token.value

    fun getStoriesWithLocation() = viewModelScope.launch {
        callStoriesWithLocation()
    }

    private suspend fun callStoriesWithLocation() {
        try {
            _storiesResult.postValue(Result.Loading)
            val response = usersRepository.getStoriesWithLocation(getToken().toString(), 1, 1)
            _storiesResult.postValue(Result.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                val error = e.response()?.errorBody()?.string()?.let { JSONObject(it) }
                _storiesResult.postValue(error?.getString("message")?.let { Result.Error(it) })
            } else _storiesResult.postValue(Result.Error(e.message().toString()))
        } catch (e: Exception) {
            _storiesResult.postValue(Result.Error(e.message.toString()))
        }
    }


}