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
import com.carrot.gallery.data.GalleryImageItemViewData
import com.carrot.gallery.data.GalleryImageItemViewDataMapper
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
        private const val UNKNOWN_PAGE = -1
    }

    val images = MutableLiveData<MutableList<Image>>()

    val imageViewDataList: LiveData<List<GalleryImageItemViewData>>

    private val currentPage = MutableLiveData<Int>()

    private val lastAddedImages = MutableLiveData<List<Image>>()

    private var isLastPage: LiveData<Boolean> = lastAddedImages.map {
        CollectionUtils.isEmpty(it) || it.size < ITEM_COUNT_PER_PAGE
    }

    private val dataFlowStatus = MutableLiveData<Result<List<Image>>>()
    val isLoading = dataFlowStatus.map { it is Result.Loading }
    val isMoreLoading = dataFlowStatus.map { it is Result.Loading && (getCurrentPage() > FIRST_IMAGE_PAGE_NO) }
    val errorViewShown = dataFlowStatus.map { isErrorOrDuringRecovery(it) }
    val emptyViewShown = dataFlowStatus.map { isEmptyResultAtFirstPage(it, getCurrentPage()) }

    // [ 고민 ] 더 좋은 방법이 있을지 고민을 하고 있습니다!
    private val dummyObserver = Observer<Any>{}


    init {
        dataFlowStatus.observeForever(dummyObserver)
        isLastPage.observeForever(dummyObserver)

        images.value = mutableListOf()
        imageViewDataList = currentPage.switchMap { page ->
            getImagesUseCase(GetImagesParameter(page, ITEM_COUNT_PER_PAGE))
                .map {
                    dataFlowStatus.value = it
                    it
                }
                .filter { it !is Result.Loading }
                .filter { it !is Result.Error }
                .filter {!isEmptyResultAtFirstPage(it, page)}
                .map {
                    // Result.Success
                    val result = it.successOr(emptyList())
                    images.value?.addAll(result)
                    lastAddedImages.value = result
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

    private fun getCurrentPage(): Int {
        return currentPage?.value ?: UNKNOWN_PAGE
    }

    private fun isEmptyResultAtFirstPage(result: Result<List<Image>>, page: Int) : Boolean {
        return result is Result.Success
                && (page == FIRST_IMAGE_PAGE_NO && CollectionUtils.isEmpty(result.data))
    }

    private fun isErrorOrDuringRecovery(result: Result<List<Image>>) : Boolean {
        return result is Result.Error
                || (result is Result.Loading && errorViewShown.value == true)
    }

    private suspend fun makeGalleryImagesFrom(data: List<Image>, page: Int): List<GalleryImageItemViewData> {
        val addedImages: List<GalleryImageItemViewData>
        withContext(idDispatcher) {
            addedImages = GalleryImageItemViewDataMapper.toSimpleImages(data)
        }

        return if (page == FIRST_IMAGE_PAGE_NO) {
            addedImages
        } else {
            val oldList = imageViewDataList.value!!
            oldList + addedImages   // memo. list reference 새로 생성
        }
    }

    fun onReceiveLoadMoreSignal() {
        if (isLastPage.value == true || dataFlowStatus.value == Result.Loading) {
            return
        }

        // memo. request next page 에 대한 usecase 의 Result.Loading 로딩 판정보다
        // onReceiveLoadMoreSignal 재호출이 빠를 수 있어서 추가했습니다.
        dataFlowStatus.value = Result.Loading
        requestNextPage()
    }

    fun onSwipeRefresh() {
        requestFirstPage()
    }

    override fun onClickSimpleImage(image: GalleryImageItemViewData.SimpleImage, position: Int) {
        notifySingleEvent(GallerySingleEventType.GoToSimpleImageViewer(image, position))
    }

    override fun onCleared() {
        super.onCleared()

        dataFlowStatus.removeObserver(dummyObserver)
        isLastPage.removeObserver(dummyObserver)
    }
}

interface GalleryItemClickListener {
    fun onClickSimpleImage(image: GalleryImageItemViewData.SimpleImage, position: Int)
}

sealed class GallerySingleEventType : SingleEventType {
    data class GoToSimpleImageViewer(val image: GalleryImageItemViewData.SimpleImage, val position: Int) : GallerySingleEventType()
}