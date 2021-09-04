package com.carrot.gallery

import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08
 */
@HiltViewModel
class SharedViewModel @Inject constructor(
) : ViewModel() {

    companion object {
        const val START_VERSION = 0
        const val KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER = "KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER"
    }

    private val dataVersions = ArrayMap<String, Int>()

    private val _galleryImages = MutableLiveData<List<Image>>()
    val galleryImages: LiveData<List<Image>>
        get() = _galleryImages

    private val _selectedPageFromImageViewer = MutableLiveData<Data<Int>>()

    /**
     * 1회성으로 notify 합니다.
     * configuration chanage 등에 대응할 수 있습니다.
     *
     * TODO : 네이밍을 SingleUseLiveData 등으로 해서 모듈화 가능할 것 같습니다.
     */
    val selectedPageFromImageViewer: LiveData<Int>
        get() = getSelectedPageFromImageViewerIfExist()



    fun onUpdateImagesAtGallery(list : List<Image>) {
        _galleryImages.value = list
    }

    fun onPageSelectedAtImageViewer(page: Int) {
        _selectedPageFromImageViewer.value = Data(page, getVersion(KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER) + 1)
    }

    private fun getSelectedPageFromImageViewerIfExist(): LiveData<Int> {
        _selectedPageFromImageViewer.value?.let { selectedPageData ->
            if (selectedPageData.version > getVersion(KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER)) {
                dataVersions[KEY_SELECTED_PAGE_FROM_IMAGE_VIEWER] = selectedPageData.version
                val data = MutableLiveData<Int>()
                data.value = selectedPageData.data
                return data
            }
        }

        // return dummy
        return MutableLiveData()
    }

    private fun getVersion(key: String): Int {
        dataVersions[key]?.let {
            return it
        }
        dataVersions[key] = START_VERSION
        return START_VERSION
    }

}

data class Data<T>(val data: T, val version: Int)
