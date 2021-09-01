package com.carrot.gallery

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.carrot.gallery.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO
 * 에러처리 변경 하기
 * 0. 인터넷 끊김 에러 캐치
 * 1. 인터넷 연결 브로드캐스트 리시버 받아서 재연결 하는 부분 개발
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