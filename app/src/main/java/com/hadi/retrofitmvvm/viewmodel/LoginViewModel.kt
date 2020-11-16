package com.hadi.retrofitmvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadi.retrofitmvvm.R
import com.hadi.retrofitmvvm.app.MyApplication
import com.hadi.retrofitmvvm.model.loginResponse.LoginResponse
import com.hadi.retrofitmvvm.network.RequestBodies
import com.hadi.retrofitmvvm.repository.AppRepository
import com.hadi.retrofitmvvm.util.Event
import com.hadi.retrofitmvvm.util.Resource
import com.hadi.retrofitmvvm.util.Utils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class LoginViewModel(
    app: Application,
    private val appRepository: AppRepository
) : AndroidViewModel(app) {

    private val _loginResponse = MutableLiveData<Event<Resource<LoginResponse>>>()
    val loginResponse:LiveData<Event<Resource<LoginResponse>>> = _loginResponse


    fun loginUser(body: RequestBodies.LoginBody) = viewModelScope.launch {
        login(body)
    }

    private suspend fun login(body: RequestBodies.LoginBody) {
        _loginResponse.postValue(Event(Resource.Loading()))
        try {
            if (Utils.hasInternetConnection(getApplication<MyApplication>())) {
                val response = appRepository.loginUser(body)
                _loginResponse.postValue(handlePicsResponse(response))
            } else {
                _loginResponse.postValue(Event(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection))))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _loginResponse.postValue(
                        Event(Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.network_failure
                            )
                        ))
                    )
                }
                else -> {
                    _loginResponse.postValue(
                        Event(Resource.Error(
                            getApplication<MyApplication>().getString(
                                R.string.conversion_error
                            )
                        ))
                    )
                }
            }
        }
    }

    private fun handlePicsResponse(response: Response<LoginResponse>): Event<Resource<LoginResponse>>? {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }
}