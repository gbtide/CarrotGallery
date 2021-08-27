package com.carrot.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.carrot.gallery.core.util.SingleLiveEvent

/**
 * Created by kyunghoon on 2021-08
 */
class MainViewModel @ViewModelInject constructor(
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

}