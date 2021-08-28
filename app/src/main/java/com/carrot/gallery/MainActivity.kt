package com.carrot.gallery

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.carrot.gallery.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * [ 앱 스펙 ]
 *
 * https://picsum.photos/
 *
 * 1.
 * api : https://picsum.photos/v2/list?page=2&limit=100
 * 이미지 가져오면 아래와 같은데
 *
 * {
 * "id":"117",
 * "author":"Daniel Ebersole",
 * "width":1544,
 * "height":1024,
 * "url":"https://unsplash.com/photos/Q14J2k8VE3U",
 * "download_url":"https://picsum.photos/id/117/1544/1024"
 * }
 *
 * 폰 해상도 가져와서 적절히 1/n 한 값으로 이미지 컨버팅해서 보여주기
 *
 * 2.
 * api : https://picsum.photos/id/870/200/300
 * 상세화면에서는 역시 폰 해상도 가져와서 적절히 보여주고,
 * 화면 효과 누르면 적절히 필터 먹여서 보여주는 정도
 *
 * 3.
 * 디바이스 가로 길이에 따라 column count 조절
 *
 * 4.
 * 이미지 상세서 줌인 기능
 *
 *
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
    }

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

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
        // do observing if needed
        //
        binding.viewModel = viewModel
    }

}