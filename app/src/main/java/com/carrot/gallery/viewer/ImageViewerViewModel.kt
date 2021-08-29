package com.carrot.gallery.viewer

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImageUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.image.ThumbnailUrlMaker
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.result.data
import com.carrot.gallery.core.result.succeeded
import com.carrot.gallery.core.util.combine
import com.carrot.gallery.util.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext private val context: Context,
    private val getImageUseCase: GetImageUseCase,
    private val thumbnailUrlMaker: ThumbnailUrlMaker,
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
) : ViewModel(), ViewModelSingleEventsDelegate by singleEventDelegate {

    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private val seekbarEventPublisher: PublishSubject<Int> = PublishSubject.create()
    private val disposable = CompositeDisposable()

    private val id = MutableLiveData<Long>()
    private val blur = MutableLiveData<Int>()
    private val grayscale = MutableLiveData<Boolean>()

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
                imageBaseUrl.value = thumbnailUrlMaker.makeUrlAdjustDevice(
                    imageViewerImage.url,
                    imageViewerImage.width,
                    imageViewerImage.height
                )
                return@map imageViewerImage

            }.asLiveData()
    }

    private val imageBaseUrl = MutableLiveData<String>()

    private val imageUrlObserver =
        imageBaseUrl.combine(blur, grayscale) { baseUrl, blurVal, grayscaleVal ->
            val uri = Uri.parse(baseUrl)
            val builder: Uri.Builder = uri.buildUpon()
            if (grayscaleVal) {
                builder.appendQueryParameter("grayscale", "")
            }
            if (blurVal > 0) {
                builder.appendQueryParameter("blur", blurVal.toString())
            }
            _isLoading.value = true
            ImageLoader.loadImageAsync(context,
                builder.toString(),
                object : ImageLoader.ImageLoadCallback {
                    override fun onFailureImageLoad(e: Throwable) {
                        // TODO
                        _isLoading.value = false
                    }

                    override fun onSuccessImageLoad(resource: Bitmap) {
                        _imageResource.value = resource
                        _isLoading.value = false
                    }
                })
            true
        }

    private val _imageResource = MutableLiveData<Bitmap>()
    val imageResource: LiveData<Bitmap>
        get() = _imageResource

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _functionBarToggler = MutableLiveData<Boolean>()
    val functionBarToggler: LiveData<Boolean>
        get() = _functionBarToggler


    init {
        blur.value = 0
        grayscale.value = false
        _functionBarToggler.value = false

        // mediator livedata 특성 때문에 필요합니다. (observer count 1 이상에서 active)
        imageUrlObserver.observeForever {}
//        observeBlurEffectValue()
    }

    fun onViewCreated(id: Long) {
        this.id.value = id
    }

//    private fun observeBlurEffectValue() {
//        disposable.add(seekbarEventPublisher
//            .debounce(500, TimeUnit.MILLISECONDS)
//            .distinctUntilChanged()
//            .doOnError {
//                //
//            }
//            .subscribeOn(AndroidSchedulers.mainThread())
//            .subscribe { blurValue ->
//                blur.value = blurValue
//            })
//    }

    fun onCloseButtonClick() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
    }

    fun onSingleTabImageEvent() {
        _functionBarToggler.value = !_functionBarToggler.value!!
    }

    fun onChangeBlurEffect(blurValue: Int) {
//        seekbarEventPublisher.onNext(value)
        blur.value = blurValue
    }

    fun onChangeGrayscaleEffect(onEffect: Boolean) {
        grayscale.value = onEffect
    }

    override fun onCleared() {
        super.onCleared()
        _imageResource.value = null
        disposable.clear()
    }

}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton : ImageViewerSingleEventType()
}