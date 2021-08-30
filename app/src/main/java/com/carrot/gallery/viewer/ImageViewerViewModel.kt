package com.carrot.gallery.viewer

import android.content.Context
import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.result.data
import com.carrot.gallery.core.result.succeeded
import com.carrot.gallery.core.util.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by kyunghoon on 2021-01-10
 */
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getImageUseCase: GetImageUseCase,
    private val imageUrlMaker: ImageUrlMaker,
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
) : ViewModel(), ViewModelSingleEventsDelegate by singleEventDelegate {
    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private val id = MutableLiveData<Long>()

    val image = id.switchMap { _id ->
        getImageUseCase(_id)
            .filter {
                // 1. Loading
                if (it is Result.Loading) {
                    _isLoading.value = true
                    return@filter false
                }
                true
            }
            .filter {
                // 2. Error
                if (it is Result.Error || !it.succeeded) {
                    // TODO
                    return@filter false
                }
                true
            }
            .map {
                // 3. Success!
                val imageViewerImage = ImageViewerImageMapper.fromImage(it.data!!)
                baseUrl.value = imageUrlMaker.addAdjustSizeParam(imageViewerImage.url, imageViewerImage.width, imageViewerImage.height)
                return@map imageViewerImage

            }.asLiveData()
    }

    private val baseUrl = MutableLiveData<String>()
    private val blurValue = MutableLiveData<Int>()
    private val useGrayscale = MutableLiveData<Boolean>()

    val imageUrl = baseUrl.combine(blurValue, useGrayscale) { _baseUrl, _blurVal, _grayscaleVal ->
        imageUrlMaker.addFilterEffectParam(_baseUrl, _blurVal, _grayscaleVal)
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _functionBarToggler = MutableLiveData<Boolean>()
    val functionBarToggler: LiveData<Boolean>
        get() = _functionBarToggler

    val enableFilterEffect = _isLoading.map {
        !it
    }

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

    fun onCloseButtonClick() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
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
        _isLoading.value = true
    }

    fun onSuccessLoadImageToView() {
        _isLoading.value = false
    }

    fun onFailureLoadImageToView() {
        _isLoading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton : ImageViewerSingleEventType()
}