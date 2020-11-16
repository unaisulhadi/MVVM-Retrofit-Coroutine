package com.hadi.retrofitmvvm.repository

import com.hadi.retrofitmvvm.network.RequestBodies
import com.hadi.retrofitmvvm.network.RetrofitInstance

class AppRepository {

    suspend fun getPictures() = RetrofitInstance.picsumApi.getPictures()

    suspend fun loginUser(body: RequestBodies.LoginBody) = RetrofitInstance.loginApi.loginUser(body)
}