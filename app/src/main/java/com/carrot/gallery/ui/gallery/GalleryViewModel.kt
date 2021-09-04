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
import timber.log.Timber
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

    private val currentPage = MutableLiveData<Int>()

    private val requestedImages = currentPage.switchMap { page ->
        getImagesUseCase(GetImagesParameter(page, ITEM_COUNT_PER_PAGE)).asLiveData()
    }

    val isMoreLoading = requestedImages.map { it is Result.Loading && (currentPage.value!! > FIRST_IMAGE_PAGE_NO) }
    val errorViewShown = requestedImages.map { isErrorOrDuringRecovery(it) }
    val emptyViewShown = requestedImages.map { isEmpty(it, currentPage.value!!) }


    val images = MutableLiveData<List<Image>>()

    private val addedImages: LiveData<List<Image>> = requestedImages.asFlow()
        .filter { it !is Result.Loading }
        .filter { it !is Result.Error }
        .map { it.successOr(emptyList()) }
        .asLiveData()

    private val addedImagesObserver = Observer<List<Image>> { _addedImages ->
        // 1) add to old-list
        val sum: List<Image> = images.value ?: mutableListOf()
        images.value = sum + _addedImages
        // 2) check "last page"
        isLastPage = _addedImages.size < ITEM_COUNT_PER_PAGE
    }


    val imageViewDataList = MutableLiveData<List<GalleryImageItemViewData>>()

    private val addedImageViewDataList: LiveData<List<GalleryImageItemViewData>> = addedImages.asFlow()
        .map { _addedImages ->
            withContext(idDispatcher) {
                GalleryImageItemViewDataMapper.toSimpleImages(_addedImages)
            }
        }.asLiveData()

    private val addedImageViewDataListObserver = Observer<List<GalleryImageItemViewData>> { _addedViewDataList ->
        // 1) add to old-list
        imageViewDataList.value = if (currentPage.value!! == FIRST_IMAGE_PAGE_NO) _addedViewDataList else imageViewDataList.value!! + _addedViewDataList
    }

    private var isLastPage = false
    private val loadMoreEventPublisher: PublishSubject<Boolean> = PublishSubject.create()
    private val disposable = CompositeDisposable()


    init {
        addedImages.observeForever(addedImagesObserver)
        addedImageViewDataList.observeForever(addedImageViewDataListObserver)

        observeLoadMoreEvent()
        requestFirstPage()
    }

    private fun observeLoadMoreEvent() {
        disposable.add(loadMoreEventPublisher.observeByDebounce(100) {
            val isError = requestedImages.value is Result.Error
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
        if (isLastPage || requestedImages.value == Result.Loading) {
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
        addedImages.removeObserver(addedImagesObserver)
        addedImageViewDataList.removeObserver(addedImageViewDataListObserver)
    }

}

interface GalleryItemClickListener {
    fun onClickSimpleImage(image: GalleryImageItemViewData.SimpleImage, position: Int)
}

sealed class GallerySingleEventType : SingleEventType {
    data class GoToSimpleImageViewer(val image: GalleryImageItemViewData.SimpleImage, val position: Int) : GallerySingleEventType()
}