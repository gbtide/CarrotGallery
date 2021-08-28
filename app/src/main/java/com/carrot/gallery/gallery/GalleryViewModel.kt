package com.carrot.gallery.gallery

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.GetImageParameter
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.core.util.SingleLiveEvent
import com.carrot.gallery.model.gallery.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by kyunghoon on 2021-08
 */
class GalleryViewModel @ViewModelInject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    @IoDispatcher private val idDispatcher: CoroutineDispatcher
) : ViewModel(), ImageClickListener {

    companion object {
        private const val FIRST_IMAGE_PAGE_NO = 1
        private const val ITEM_COUNT_PER_PAGE = 30
    }

    val images: LiveData<List<Any>>

    private val lastAddedImages = MutableLiveData<List<Any>>()

    private var isLastPage: LiveData<Boolean> = lastAddedImages.map {
        CollectionUtils.isEmpty(it) || it.size < ITEM_COUNT_PER_PAGE
    }

    private val currentPage = MutableLiveData<Int>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val isEmpty = MutableLiveData<Boolean>()

    private val _goToImageViewerAction = SingleLiveEvent<GalleryImage>()
    val goToImageViewerAction: LiveData<GalleryImage>
        get() = _goToImageViewerAction

//    val imageResult: LiveData<Result<Image>> = liveData {
//        emit(getImageUseCase(1))
//    }

    init {
        _isLoading.value = false

        images = currentPage.switchMap { page ->
            getImagesUseCase(GetImageParameter(page, ITEM_COUNT_PER_PAGE))
                .filter {
                    if (it is Result.Loading) {
                        _isLoading.value = true
                        return@filter false
                    }
                    true
                }
                .map {
                    if (it is Result.Success) {
                        lastAddedImages.value = it.data
                        return@map makeGalleryImagesFrom(it.data, page)

                    } else if (it is Result.Error) {
                        return@map emptyList<Any>()

                    }
                    throw IllegalArgumentException("Unknown Status")

                }.map {
                    _isLoading.value = false
                    return@map it
                }
                .asLiveData()
        }

        requestFirstPage()
    }

    private fun requestFirstPage() {
        currentPage.value = FIRST_IMAGE_PAGE_NO
    }

    private fun requestNextPage() {
        currentPage.value = currentPage.value!! + 1
    }

    private suspend fun makeGalleryImagesFrom(data: List<Image>, page: Int): List<Any> {
        val addedImages: List<GalleryImage>
        withContext(idDispatcher) {
            addedImages = GalleryImageMapper.fromImages(data)
        }

        return if (page == FIRST_IMAGE_PAGE_NO) {
            addedImages
        } else {
            val oldList = images.value!!
            oldList + addedImages
        }
    }

    fun onReceiveLoadMoreSignal() {
        Timber.d("#### onReceiveLoadMoreSignal")
        if (isLastPage.value == true || _isLoading.value == true) {
            return
        }

        _isLoading.value = true // memo. Result.Loading 로딩 판정보다 onReceiveLoadMoreSignal 재호출이 빠를 수 있어서 추가했습니다.
        requestNextPage()
    }

    fun onSwipeRefresh() {
        requestFirstPage()
    }

    override fun onClickImage(image: GalleryImage) {
        _goToImageViewerAction.value = image
    }
}

interface ImageClickListener {
    fun onClickImage(image: GalleryImage)
}