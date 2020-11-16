package com.hadi.retrofitmvvm.network

object RequestBodies {

    data class LoginBody(
        val email:String,
        val password:String
    )

}