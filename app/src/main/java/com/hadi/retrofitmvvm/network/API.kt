package com.hadi.retrofitmvvm.network

import com.hadi.retrofitmvvm.model.PicsResponse
import retrofit2.Response
import retrofit2.http.GET

interface API {

    @GET("v2/list")
    suspend fun getPictures(): Response<PicsResponse>
}