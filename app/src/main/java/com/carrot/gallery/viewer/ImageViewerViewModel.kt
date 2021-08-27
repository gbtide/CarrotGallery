package com.carrot.gallery.viewer

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.carrot.gallery.model.gallery.Image

/**
 * Created by kyunghoon on 2021-01-10
 */
class ImageViewerViewModel @ViewModelInject constructor(
) : ViewModel() {

    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private var image: Image? = null

    fun onViewCreated(image: Image?) {
        this.image = image
    }

}