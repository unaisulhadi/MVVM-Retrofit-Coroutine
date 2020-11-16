package com.hadi.retrofitmvvm.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hadi.retrofitmvvm.R
import com.hadi.retrofitmvvm.network.RequestBodies
import com.hadi.retrofitmvvm.repository.AppRepository
import com.hadi.retrofitmvvm.util.Resource
import com.hadi.retrofitmvvm.util.errorSnack
import com.hadi.retrofitmvvm.viewmodel.LoginViewModel
import com.hadi.retrofitmvvm.viewmodel.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        val repository = AppRepository()
        val factory = ViewModelProviderFactory(application, repository)
        loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
    }

    fun onLoginClick(view: View) {
        var email = edt_mail.text.toString()
        val password = edt_pass.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            val body = RequestBodies.LoginBody(
                email,
                password
            )

            loginViewModel.loginUser(body)
            loginViewModel.loginResponse.observe(this, Observer { event ->
                event.getContentIfNotHandled()?.let { response ->
                    when (response) {
                        is Resource.Success -> {
                            hideProgressBar()
                            response.data?.let { loginResponse ->
                                Intent(this@LoginActivity, MainActivity::class.java).also {
                                    startActivity(it)
                                }
                            }
                        }

                        is Resource.Error -> {
                            hideProgressBar()
                            response.message?.let { message ->
                                progress.errorSnack(message, Snackbar.LENGTH_LONG)
                            }
                        }

                        is Resource.Loading -> {
                            showProgressBar()
                        }
                    }
                }
            })
        }
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }


}