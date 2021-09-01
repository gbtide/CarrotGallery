package com.carrot.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08
 */
@HiltViewModel
class SharedViewModel @Inject constructor(
) : ViewModel() {

    private val _sharedList = MutableLiveData<List<Any>>()
    val sharedList: LiveData<List<Any>>
        get() = _sharedList

    fun onUpdateListAtGallery(list : List<Any>) {
        _sharedList.value = list
    }

    fun onUpdateListAtImageViewer(list : List<Any>) {
        _sharedList.value = list
    }

}