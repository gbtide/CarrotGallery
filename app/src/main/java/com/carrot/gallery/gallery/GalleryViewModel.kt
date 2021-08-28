package com.carrot.gallery.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImageParameter
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.util.SingleLiveEvent
import com.carrot.gallery.core.util.combine
import com.carrot.gallery.core.util.map
import com.carrot.gallery.model.gallery.Image
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Created by kyunghoon on 2021-08
 */
class GalleryViewModel @ViewModelInject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val getImageUseCase: GetImageUseCase
) : ViewModel(), ImageClickListener {

    companion object {
        private const val FIRST_IMAGE_PAGE_NO = 1
        private const val TAG = "ImageListViewModel"
        private const val IMAGE_COUNT_PER_PAGE = "ImageCountPerPage"
    }

    private val currentPage = MutableLiveData<Int>()

    lateinit var imageList: LiveData<List<Any>>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val dispatchSwipe = MutableLiveData<Boolean>()

    val swipeRefreshing = dispatchSwipe.combine(_isLoading) { bySwipe, isLoading ->
        bySwipe && isLoading
    }

    val isEmpty = MutableLiveData<Boolean>()

    private val _goToImageViewerAction = SingleLiveEvent<GalleryImage>()
    val goToImageViewerAction: LiveData<GalleryImage>
        get() = _goToImageViewerAction

//    val imageResult: LiveData<Result<Image>> = liveData {
//        emit(getImageUseCase(1))
//    }
//
//    val imagesResult: LiveData<Result<List<Image>>> = liveData {
//        emit(getImagesUseCase(GetImageParameter(1, 30)))
//    }



    init {
        imageList = currentPage.switchMap { page ->
            getImagesUseCase(GetImageParameter(page, 30))
                .filter {
                    if (it is Result.Loading) {
                        _isLoading.value = true
                        return@filter false
                    }
                    true
                }
                .map {
                    _isLoading.value = false

                    if (it is Result.Success) {
                        val addedImages = GalleryImageMapper.fromImages(it.data)
                        imageList.value?.let { oldList -> return@map oldList + addedImages }
                        return@map addedImages

                    } else if (it is Result.Error) {
                        // 에러뷰 노출
                        return@map mutableListOf()
                    }
                    throw IllegalArgumentException("Unknown Status")
                }
                .asLiveData()
        }

        currentPage.value = FIRST_IMAGE_PAGE_NO
    }

    fun onSwipeRefresh() {
        _isLoading.value = true
        dispatchSwipe.value = true
        refreshList()
    }

    private fun refreshList() {
    }

    override fun onClickImage(image: GalleryImage) {
        _goToImageViewerAction.value = image
    }
}

interface ImageClickListener {
    fun onClickImage(image: GalleryImage)
}