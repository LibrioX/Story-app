package com.example.dicodingstoryapp.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.api.config.BasicResponse
import com.example.dicodingstoryapp.repository.UsersRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import com.example.dicodingstoryapp.repository.Result

class RegisterViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<BasicResponse>>()
    val registerResult: LiveData<Result<BasicResponse>> = _registerResult

    private suspend fun callRegisterUser(username: String, email: String, password: String) {
        try {

            _registerResult.postValue(Result.Loading)
            val response = usersRepository.registerUser(username, email, password)
            _registerResult.postValue(Result.Success(response))

        } catch (e: HttpException) {
            if (e.code() == 400) {
                val error = e.response()?.errorBody()?.string()?.let { JSONObject(it) }
                _registerResult.postValue(error?.getString("message")?.let { Result.Error(it) })
            } else _registerResult.postValue(Result.Error(e.message().toString()))
        } catch (e: Exception) {
            _registerResult.postValue(Result.Error(e.message.toString()))
        }
    }

    fun registerUser(username: String, email: String, password: String) = viewModelScope.launch {
        callRegisterUser(username, email, password)
    }

}