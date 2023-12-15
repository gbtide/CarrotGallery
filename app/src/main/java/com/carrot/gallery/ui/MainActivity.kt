package com.carrot.gallery.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.carrot.gallery.R
import com.carrot.gallery.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author kyunghoon
 * 메인
 * revert 1
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        init()
    }

    private fun init() {
        initViewModel()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
    }
}