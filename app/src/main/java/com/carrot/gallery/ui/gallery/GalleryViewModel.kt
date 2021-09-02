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
import com.carrot.gallery.core.util.notifyObserver
import com.carrot.gallery.core.util.observeByDebounce
import com.carrot.gallery.data.GalleryImageItemViewData
import com.carrot.gallery.data.GalleryImageItemViewDataMapper
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
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

    val images = MutableLiveData<MutableList<Image>>()
    val imageViewDataList: LiveData<List<GalleryImageItemViewData>>

    private val currentPage = MutableLiveData<Int>()
    private var isLastPage = false

    private val dataFlowStatus = MutableLiveData<Result<List<Image>>>()
    val isMoreLoading = dataFlowStatus.map { it is Result.Loading && (currentPage.value!! > FIRST_IMAGE_PAGE_NO) }
    val errorViewShown = dataFlowStatus.map { isErrorOrDuringRecovery(it) }
    val emptyViewShown = dataFlowStatus.map { isEmpty(it, currentPage.value!!) }

    private val loadMoreEventPublisher: PublishSubject<Boolean> = PublishSubject.create()
    private val disposable = CompositeDisposable()

    private val dummyObserver = Observer<Any> {}


    init {
        images.value = mutableListOf()
        imageViewDataList = currentPage.switchMap { page ->
            getImagesUseCase(GetImagesParameter(page, ITEM_COUNT_PER_PAGE))
                .map {
                    dataFlowStatus.value = it
                    it
                }
                .filter { it !is Result.Loading }
                .filter { it !is Result.Error }
                .map { it.successOr(emptyList()) }
                .map {
                    images.value!!.addAll(it)
                    isLastPage = it.size < ITEM_COUNT_PER_PAGE
                    it
                }
                .map { mapToViewDataList(it) }
                .map { mergeToCurrentViewDataList(it, page) }
                .asLiveData()
        }

        observeLoadMoreEvent()
        dataFlowStatus.observeForever(dummyObserver)

        requestFirstPage()
    }

    private suspend fun mapToViewDataList(result: List<Image>): List<GalleryImageItemViewData> {
        return withContext(idDispatcher) {
            GalleryImageItemViewDataMapper.toSimpleImages(result)
        }
    }

    private fun mergeToCurrentViewDataList(addedImages: List<GalleryImageItemViewData>, page: Int): List<GalleryImageItemViewData> {
        return if (page == FIRST_IMAGE_PAGE_NO) {
            addedImages
        } else {
            val currentImages = imageViewDataList.value!!
            currentImages + addedImages
        }
    }

    private fun observeLoadMoreEvent() {
        disposable.add(loadMoreEventPublisher.observeByDebounce(500) {
            val isError = dataFlowStatus.value is Result.Error
            if (isError) {
                retryPage()
            } else {
                requestNextPage()
            }
        })
    }

    private fun requestFirstPage() {
        currentPage.value = FIRST_IMAGE_PAGE_NO
    }

    private fun requestNextPage() {
        currentPage.value = currentPage.value!! + 1
    }

    private fun retryPage() {
        currentPage.notifyObserver()
    }

    private fun isEmpty(result: Result<List<Image>>, page: Int): Boolean {
        return result is Result.Success
                && (page == FIRST_IMAGE_PAGE_NO && CollectionUtils.isEmpty(result.data))
    }

    private fun isErrorOrDuringRecovery(result: Result<List<Image>>): Boolean {
        return result is Result.Error
                || (result is Result.Loading && errorViewShown.value == true)
    }

    fun onReceiveLoadMoreSignal() {
        if (isLastPage || dataFlowStatus.value == Result.Loading) {
            return
        }
        loadMoreEventPublisher.onNext(true)
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
    }
}

interface GalleryItemClickListener {
    fun onClickSimpleImage(image: GalleryImageItemViewData.SimpleImage, position: Int)
}

sealed class GallerySingleEventType : SingleEventType {
    data class GoToSimpleImageViewer(val image: GalleryImageItemViewData.SimpleImage, val position: Int) : GallerySingleEventType()
}