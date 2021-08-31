package com.carrot.gallery.ui.viewer

import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.core.result.*
import com.carrot.gallery.core.util.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by kyunghoon on 2021-01-10
 */
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val getImageUseCase: GetImageUseCase,
    private val imageUrlMaker: ImageUrlMaker,
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
) : ViewModel(), ViewModelSingleEventsDelegate by singleEventDelegate {
    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private val id = MutableLiveData<Long>()
    private val dataFlowStatus = MutableLiveData<Result<*>>()

    val image = id.switchMap { _id ->
        getImageUseCase(_id)
            .map {
                dataFlowStatus.value = it
                it
            }
            .filter { it !is Result.Loading }
            .filter { it !is Result.Error }
            .map {
                val imageViewerImage = ImageViewerImageMapper.fromImage(it.data!!)
                baseImageUrl.value = imageUrlMaker.addAdjustSizeParam(imageViewerImage.downloadUrl, imageViewerImage.width, imageViewerImage.height)
                return@map imageViewerImage

            }.asLiveData()
    }

    private val baseImageUrl = MutableLiveData<String>()
    private val blurValue = MutableLiveData<Int>()
    private val useGrayscale = MutableLiveData<Boolean>()

    val imageUrl = baseImageUrl.combine(blurValue, useGrayscale) { _baseUrl, _blurVal, _grayscaleVal ->
        imageUrlMaker.addFilterEffectParam(_baseUrl, _blurVal, _grayscaleVal)
    }

    val isLoading = dataFlowStatus.map { it is Result.Loading }

    val errorViewShown = dataFlowStatus.map { it is Result.Error }

    private val _functionBarToggler = MutableLiveData<Boolean>()
    val functionBarToggler: LiveData<Boolean>
        get() = _functionBarToggler

    val enableFilterEffect = isLoading.map { !it }

    private val seekbarEventPublisher: PublishSubject<Int> = PublishSubject.create()
    private val disposable = CompositeDisposable()

    init {
        blurValue.value = 0
        useGrayscale.value = false
        _functionBarToggler.value = false

        observeBlurEffectValue()
    }

    fun onViewCreated(id: Long) {
        this.id.value = id
    }

    private fun observeBlurEffectValue() {
        disposable.add(seekbarEventPublisher
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe { blurValue ->
                this.blurValue.postValue(blurValue)
            })
    }

    fun onClickCloseButton() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
    }

    fun onClickMoreButton(url: String) {
        notifySingleEvent(ImageViewerSingleEventType.ClickMoreButton(url))
    }

    fun onSingleTabImageEvent() {
        _functionBarToggler.value = !_functionBarToggler.value!!
    }

    fun onChangeBlurEffect(blurValue: Int) {
        seekbarEventPublisher.onNext(blurValue)
    }

    fun onChangeGrayscaleEffect(onEffect: Boolean) {
        useGrayscale.value = onEffect
    }

    fun onStartLoadImageToView() {
        dataFlowStatus.value = Result.Loading
    }

    fun onSuccessLoadImageToView() {
        dataFlowStatus.value = Result.createEmptySuccess()
    }

    fun onFailureLoadImageToView(e: Throwable) {
        dataFlowStatus.value = Result.Error(e)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton : ImageViewerSingleEventType()
    data class ClickMoreButton(val url: String) : ImageViewerSingleEventType()
}