package com.carrot.gallery.ui.viewer

import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by kyunghoon on 2021-01-10
 */
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
) : ViewModel(), ImageViewerViewEventListener, ViewModelSingleEventsDelegate by singleEventDelegate {
    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private var position: Int = 0

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>>
        get() = _images

    private var oldImages: List<Image>? = null

    private val imageDiff: LiveData<List<Image>>

    val imageViewDataList: LiveData<List<ImageViewerViewData>>

    private val _currentImage = MutableLiveData<ImageViewerViewData>()
    val currentImage: LiveData<ImageViewerViewData>
        get() = _currentImage

    private val dataFlowStatus = MutableLiveData<Result<Any>>()
    val isLoading = dataFlowStatus.map { it is Result.Loading }
    val errorViewShown = dataFlowStatus.map { it is Result.Error }

    private val _functionBarToggler = MutableLiveData<Boolean>()
    val functionBarToggler: LiveData<Boolean>
        get() = _functionBarToggler

    val enableFilterEffect = isLoading.map { !it }

    private val seekbarEventPublisher: PublishSubject<Int> = PublishSubject.create()
    private val disposable = CompositeDisposable()

    init {
        _functionBarToggler.value = false

        observeBlurEffectValue()

        imageDiff = images.map {
            val oldSize = oldImages?.size ?: 0
            val diff = if (it.size > oldSize) it.subList(oldSize, it.size) else emptyList()
            oldImages = it
            return@map diff
        }

        imageViewDataList = imageDiff.map { diff ->
            return@map sumToImageViewDataList(diff)
        }

        // 임시 코
        dataFlowStatus.value = Result.Success(Any())
    }

    fun onViewCreated(position: Int) {
        this.position = position
    }

    fun onInitImages(images: List<Image>) {
        this._images.value = images
    }

    private fun sumToImageViewDataList(diff: List<Image>): List<ImageViewerViewData> {
        val oldList: List<ImageViewerViewData> = imageViewDataList.value ?: emptyList()
        return oldList + ImageViewerViewDataMapper.toImageViewerViewDataList(diff)
    }

    private fun observeBlurEffectValue() {
        disposable.add(seekbarEventPublisher
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { blurValue ->
                _currentImage.value?.blur = blurValue
                notifySingleEvent(ImageViewerSingleEventType.NotifyDataChange)
            })
    }

    fun onClickCloseButton() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
    }

    fun onClickMoreButton(image: ImageViewerViewData) {
        notifySingleEvent(ImageViewerSingleEventType.ClickMoreButton(image.externalLinkUrl))
    }

    override fun onSingleTabImageEvent() {
        _functionBarToggler.value = !_functionBarToggler.value!!
    }

    fun onChangeBlurEffect(blurValue: Int) {
        seekbarEventPublisher.onNext(blurValue)
    }

    fun onChangeGrayscaleEffect(onEffect: Boolean) {
        _currentImage.value?.grayscale = onEffect
        notifySingleEvent(ImageViewerSingleEventType.NotifyDataChange)
    }

    fun onChangePosition(position: Int) {
        this.position = position
        _currentImage.value = this.imageViewDataList.value!![position]
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}

interface ImageViewerViewEventListener {
    fun onSingleTabImageEvent()
}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton : ImageViewerSingleEventType()
    data class ClickMoreButton(val url: String) : ImageViewerSingleEventType()
    object NotifyDataChange : ImageViewerSingleEventType()
}
