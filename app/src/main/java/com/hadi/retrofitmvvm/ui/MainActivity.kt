package com.hadi.retrofitmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hadi.retrofitmvvm.R
import com.hadi.retrofitmvvm.adapter.PicsAdapter
import com.hadi.retrofitmvvm.repository.AppRepository
import com.hadi.retrofitmvvm.util.Resource
import com.hadi.retrofitmvvm.util.action
import com.hadi.retrofitmvvm.util.errorSnack
import com.hadi.retrofitmvvm.viewmodel.PicsViewModel
import com.hadi.retrofitmvvm.viewmodel.PicsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PicsViewModel
    lateinit var picsAdapter: PicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        init()
        getPictures()
    }

    private fun init() {
        rvPics.setHasFixedSize(true)
        rvPics.layoutManager = LinearLayoutManager(this)
        picsAdapter = PicsAdapter()
    }

    private fun setupViewModel() {
        val repository = AppRepository()
        val factory = PicsViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, factory).get(PicsViewModel::class.java)
    }

    private fun getPictures() {
        viewModel.picsData.observe(this, Observer { response ->
            when (response) {

                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { picsResponse ->
                        picsAdapter.differ.submitList(picsResponse)
                        rvPics.adapter = picsAdapter
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        progress.errorSnack(message,Snackbar.LENGTH_LONG)
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        progress.visibility = View.VISIBLE
    }


    fun onProgressClick(view: View) {
        //Preventing Click during loading
    }
}