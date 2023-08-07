package com.example.dicodingstoryapp.fragment.showStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.api.model.ResponseStory
import com.example.dicodingstoryapp.repository.Result
import com.example.dicodingstoryapp.repository.UsersRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class ShowStoryViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _storyDetailResult = MutableLiveData<Result<ResponseStory>>()
    val storyDetailResult: LiveData<Result<ResponseStory>> = _storyDetailResult

    private val _id = MutableLiveData<String>()
    private val _token = MutableLiveData<String>()

    private fun getId() = _id.value
    fun setId(id: String) {
        _id.value = id
    }

    private fun getToken() = _token.value
    fun setToken(username: String) {
        _token.value = username
    }

    fun getDetailStories() = viewModelScope.launch {
        callDetailStories()
    }

    private suspend fun callDetailStories() {
        try {
            _storyDetailResult.postValue(Result.Loading)
            val response = usersRepository.getDetailStory(getId().toString(), getToken().toString())
            _storyDetailResult.postValue(Result.Success(response))

        } catch (e: HttpException) {
            if (e.code() == 401) {
                val error = e.response()?.errorBody()?.string()?.let { JSONObject(it) }
                _storyDetailResult.postValue(error?.getString("message")?.let { Result.Error(it) })
            } else _storyDetailResult.postValue(Result.Error(e.message().toString()))
        } catch (e: Exception) {
            _storyDetailResult.postValue(Result.Error(e.message.toString()))
        }
    }

}