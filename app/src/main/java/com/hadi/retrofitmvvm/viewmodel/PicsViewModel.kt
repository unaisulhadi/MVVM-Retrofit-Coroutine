package com.hadi.retrofitmvvm.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hadi.retrofitmvvm.R
import com.hadi.retrofitmvvm.app.MyApplication
import com.hadi.retrofitmvvm.model.PicsResponse
import com.hadi.retrofitmvvm.repository.AppRepository
import com.hadi.retrofitmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class PicsViewModel(
    app: Application,
    private val appRepository: AppRepository
) : AndroidViewModel(app) {

    val picsData :MutableLiveData<Resource<PicsResponse>> = MutableLiveData()

    init {
        getPictures()
    }

    fun getPictures() = viewModelScope.launch {
        fetchPics()
    }

    private suspend fun fetchPics() {
        picsData.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = appRepository.getPictures()
                picsData.postValue(handlePicsResponse(response))
            }else{
                picsData.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection)))
            }
        }catch(t: Throwable) {
            when(t) {
                is IOException -> picsData.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.network_failure)))
                else -> picsData.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.conversion_error)))
            }
        }
    }

    private fun handlePicsResponse(response: Response<PicsResponse>): Resource<PicsResponse> {
        if(response.isSuccessful){
            response.body()?.let {resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    //Check Internet Connection
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}