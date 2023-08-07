package com.example.dicodingstoryapp.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.api.model.ResponseUser
import com.example.dicodingstoryapp.repository.UsersRepository
import com.example.dicodingstoryapp.repository.Result
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class LoginViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<ResponseUser>>()
    val loginResult: LiveData<Result<ResponseUser>> = _loginResult

    private suspend fun callLoginUser(email: String, password: String) {
        try {
            _loginResult.postValue(Result.Loading)
            val response = usersRepository.loginUser(email, password)
            _loginResult.postValue(Result.Success(response))
        } catch (e: HttpException) {
            if (e.code() == 401) {
                val error = e.response()?.errorBody()?.string()?.let { JSONObject(it) }
                _loginResult.postValue(error?.getString("message")?.let { Result.Error(it) })
            } else _loginResult.postValue(Result.Error(e.message().toString()))
        } catch (e: Exception) {
            _loginResult.postValue(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        callLoginUser(email, password)
    }

}