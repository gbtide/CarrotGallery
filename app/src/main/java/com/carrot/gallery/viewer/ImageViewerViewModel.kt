package com.carrot.gallery.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-01-10
 */
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val getImageUseCase: GetImageUseCase,
    @IoDispatcher private val idDispatcher: CoroutineDispatcher,
    private val singleEventDelegate: ViewModelSingleEventsDelegate
) : ViewModel(), ViewModelSingleEventsDelegate by singleEventDelegate {

    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private val _image = MutableLiveData<Image>()
    val image: LiveData<Image>
        get() = _image

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun onViewCreated(id: Long) {
        viewModelScope.launch {
            // main thread

            getImageUseCase(id)
                .filter {
                    if (it is Result.Loading) {
                        _isLoading.value = true
                        return@filter false
                    }
                    true
                }
                .map {
                    if (it is Result.Success) {
                        return@map it.data

                    } else if (it is Result.Error) {
                        return@map null

                    }
                    throw IllegalArgumentException("Unknown Status")

                }.map {
                    _isLoading.value = false
                    return@map it
                }.collect {
                    // main thread

                    it?.let {
                        _image.value = it
                    }
                }
        }
    }

    fun onStartImageLoad() {
        _isLoading.value = true
    }

    fun onImageLoadFailed() {
        _isLoading.value = false
    }

    fun onImageResourceReady() {
        _isLoading.value = false
    }

    fun onCloseButtonClick() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
    }

}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton: ImageViewerSingleEventType()
}