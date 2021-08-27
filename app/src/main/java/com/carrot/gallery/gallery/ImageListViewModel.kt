package com.carrot.gallery.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.carrot.gallery.core.domain.GetImageParameter
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.util.SingleLiveEvent
import com.carrot.gallery.core.util.combine
import com.carrot.gallery.model.gallery.Image

/**
 * Created by kyunghoon on 2021-08
 */
class ImageListViewModel @ViewModelInject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val getImageUseCase: GetImageUseCase
) : ViewModel(), ImageClickListener {

    companion object {
        private const val TAG = "ImageListViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val dispatchSwipe = MutableLiveData<Boolean>()

    val swipeRefreshing = dispatchSwipe.combine(_isLoading) { bySwipe, isLoading ->
        bySwipe && isLoading
    }

    val isEmpty = MutableLiveData<Boolean>()

    private val _goToImageViewerAction = SingleLiveEvent<Image>()
    val goToImageViewerAction: LiveData<Image>
        get() = _goToImageViewerAction

    val imageResult: LiveData<Result<Image>> = liveData {
        emit(getImageUseCase(1))
    }

    val imagesResult: LiveData<Result<List<Image>>> = liveData {
        emit(getImagesUseCase(GetImageParameter(1, 30)))
    }

    fun onSwipeRefresh() {
        _isLoading.value = true
        dispatchSwipe.value = true
        refreshList()
    }

    private fun refreshList() {
    }

    override fun onClickImage(image: Image) {
        _goToImageViewerAction.value = image
    }
}

interface ImageClickListener {
    fun onClickImage(image: Image)
}