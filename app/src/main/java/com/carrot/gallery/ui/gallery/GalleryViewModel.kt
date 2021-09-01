package com.carrot.gallery.ui.gallery

import androidx.lifecycle.*
import com.carrot.gallery.core.di.IoDispatcher
import com.carrot.gallery.core.domain.GetImagesParameter
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.result.successOr
import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08
 */
@HiltViewModel
class GalleryViewModel @Inject constructor(
    @IoDispatcher private val idDispatcher: CoroutineDispatcher,
    private val getImagesUseCase: GetImagesUseCase,
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
) : ViewModel(), GalleryItemClickListener, ViewModelSingleEventsDelegate by singleEventDelegate {

    companion object {
        private const val FIRST_IMAGE_PAGE_NO = 1
        private const val ITEM_COUNT_PER_PAGE = 30
    }

    val images: LiveData<List<Any>>

    private val currentPage = MutableLiveData<Int>()

    private val lastAddedItems = MutableLiveData<List<Any>>()

    private var isLastPage: LiveData<Boolean> = lastAddedItems.map {
        CollectionUtils.isEmpty(it) || it.size < ITEM_COUNT_PER_PAGE
    }

    private val dataFlowStatus = MutableLiveData<Result<*>>()
    val isLoading = dataFlowStatus.map { it is Result.Loading }

    val errorViewShown = dataFlowStatus.map { it is Result.Error }
    val emptyViewShown = MutableLiveData<Boolean>()

    init {
        images = currentPage.switchMap { page ->
            getImagesUseCase(GetImagesParameter(page, ITEM_COUNT_PER_PAGE))
                .map {
                    dataFlowStatus.value = it
                    it
                }
                .filter { it !is Result.Loading }
                .filter { it !is Result.Error }
                .filter {
                    val emptyFromFirstPage = it is Result.Success && (page == FIRST_IMAGE_PAGE_NO && CollectionUtils.isEmpty(it.data))
                    emptyViewShown.value = emptyFromFirstPage
                    return@filter !emptyFromFirstPage
                }
                .map {
                    val result = it.successOr(emptyList())
                    lastAddedItems.value = result
                    return@map makeGalleryImagesFrom(result, page)

                }.asLiveData()
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
            oldList + addedImages   // memo. list reference 새로 생성
        }
    }

    fun onReceiveLoadMoreSignal() {
        if (isLastPage.value == true || isLoading.value == true) {
            return
        }

        // memo. usecase 의 Result.Loading 로딩 판정보다 onReceiveLoadMoreSignal 재호출이 빠를 수 있어서 추가했습니다.
        dataFlowStatus.value = Result.Loading
        requestNextPage()
    }

    fun onSwipeRefresh() {
        requestFirstPage()
    }

    override fun onClickImage(image: GalleryImage, position: Int) {
        notifySingleEvent(GallerySingleEventType.GoToImageViewer(image, position))
    }
}

interface GalleryItemClickListener {
    fun onClickImage(image: GalleryImage, position: Int)
}

sealed class GallerySingleEventType : SingleEventType {
    data class GoToImageViewer(val image: GalleryImage, val position: Int) : GallerySingleEventType()
}