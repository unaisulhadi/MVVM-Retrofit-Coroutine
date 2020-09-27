package com.hadi.retrofitmvvm.repository

import com.hadi.retrofitmvvm.network.RetrofitInstance

class AppRepository {

    suspend fun getPictures() = RetrofitInstance.api.getPictures()

}