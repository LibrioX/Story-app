package com.example.dicodingstoryapp.fragment.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.api.config.BasicResponse
import com.example.dicodingstoryapp.repository.UsersRepository
import com.example.dicodingstoryapp.repository.Result
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class AddStoryViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _postStoryResult = MutableLiveData<Result<BasicResponse>>()
    val postStoryResult: LiveData<Result<BasicResponse>> = _postStoryResult

    private val _token = MutableLiveData<String>()
    private val _file = MutableLiveData<MultipartBody.Part>()
    private val _description = MutableLiveData<RequestBody>()
    private val _lat = MutableLiveData<RequestBody>()
    private val _long = MutableLiveData<RequestBody>()

    private fun getToken() = _token.value
    fun setToken(token: String) {
        _token.value = token
    }

    private fun getFile() = _file.value
    fun setFile(file: MultipartBody.Part) {
        _file.value = file
    }

    private fun getDescription() = _description.value
    fun setDescription(description: RequestBody) {
        _description.value = description
    }

    private fun getLat() = _lat.value
    fun setLat(lat: RequestBody) {
        _lat.value = lat
    }

    private fun getLon() = _long.value
    fun setLon(lon: RequestBody) {
        _long.value = lon
    }

    fun postStory() = viewModelScope.launch {
        callPostStory()
    }

    private suspend fun callPostStory() {
        try {
            _postStoryResult.postValue(Result.Loading)
            val file = getFile()
            val description = getDescription()
            val lat = getLat()
            val lon = getLon()
            if (file != null && description != null) {
                val response = usersRepository.postStoriesWithLocation(
                    getToken().toString(),
                    file,
                    description,
                    lat,
                    lon
                )
                _postStoryResult.postValue(Result.Success(response))
            }

        } catch (e: HttpException) {
            if (e.code() == 401) {
                val error = e.response()?.errorBody()?.string()?.let { JSONObject(it) }
                _postStoryResult.postValue(error?.getString("message")?.let { Result.Error(it) })
            } else _postStoryResult.postValue(Result.Error(e.message().toString()))
        } catch (e: Exception) {
            _postStoryResult.postValue(Result.Error(e.message.toString()))
        }
    }

}