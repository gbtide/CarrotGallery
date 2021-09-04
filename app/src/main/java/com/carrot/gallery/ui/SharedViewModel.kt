package com.carrot.gallery.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carrot.gallery.core.event.Data
import com.carrot.gallery.core.event.OneTimeReturnableLiveDataContainer
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08
 */
@HiltViewModel
class SharedViewModel @Inject constructor(
    oneTimeReturnableLiveDataContainer: OneTimeReturnableLiveDataContainer
) : ViewModel(), OneTimeReturnableLiveDataContainer by oneTimeReturnableLiveDataContainer {

    companion object {
        const val KEY_GALLERY_IMAGES_FROM_GALLERY = "KEY_GALLERY_IMAGES_FROM_GALLERY"
        const val KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER = "KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER"
    }

    private val _galleryImagesFromGallery = MutableLiveData<Data<List<Image>>>()
    val galleryImagesFromGallery: LiveData<List<Image>>
        get() = getOneTimeReturnableLiveData(_galleryImagesFromGallery, KEY_GALLERY_IMAGES_FROM_GALLERY)

    private val _selectedPageFromImageViewer = MutableLiveData<Data<Int>>()
    val selectedPageFromImageViewer: LiveData<Int>
        get() = getOneTimeReturnableLiveData(_selectedPageFromImageViewer, KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER)


    fun onUpdateImagesAtGallery(list: List<Image>) {
        _galleryImagesFromGallery.value = createDataForOneTimeReturnableLiveData(list, KEY_GALLERY_IMAGES_FROM_GALLERY)
    }

    fun onPageSelectedAtImageViewer(page: Int) {
        _selectedPageFromImageViewer.value = createDataForOneTimeReturnableLiveData(page, KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER)
    }

}

