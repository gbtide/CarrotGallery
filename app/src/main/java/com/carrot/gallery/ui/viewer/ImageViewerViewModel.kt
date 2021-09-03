package com.carrot.gallery.ui.viewer

import androidx.lifecycle.*
import com.carrot.gallery.core.domain.GetImagesUseCase
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.core.util.observeByDebounce
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
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
) : ViewModel(), ImageViewerSinglePageListener, ViewModelSingleEventsDelegate by singleEventDelegate {
    companion object {
        private const val TAG = "ImageViewerViewModel"
    }

    private var position: Int = 0

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>>
        get() = _images

    private var oldImages: List<Image>? = null

    private val imageDiff: LiveData<List<Image>> = images.map {
        val oldSize = oldImages?.size ?: 0
        val diff = if (it.size > oldSize) it.subList(oldSize, it.size) else emptyList()
        oldImages = it
        return@map diff
    }

    val imageViewDataList = imageDiff.map { diff ->
        return@map sumToImageViewDataList(diff)
    }

    private val _currentImage = MutableLiveData<ImageViewerViewData>()
    val currentImage: LiveData<ImageViewerViewData>
        get() = _currentImage

    private val _functionBarToggler = MutableLiveData<Boolean>()
    val functionBarToggler: LiveData<Boolean>
        get() = _functionBarToggler

    private val blurEffectSeekEventPublisher: PublishSubject<Int> = PublishSubject.create()
    private val grayscaleSwitchEventPublisher: PublishSubject<Boolean> = PublishSubject.create()
    private val disposable = CompositeDisposable()


    init {
        _functionBarToggler.value = true

        observeBlurEffectValue()
        observeGrayscaleEffectValue()
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

    /**
     * memo. ImageViewerViewData 내부 blur 를 ObservableInt, ObservableBoolean 로 정리하면 ReloadImage 싱글 이벤트 액션을 없앨 수는 있으나
     * 성능, 가독성 면에서 지금도 나쁘지 않다고 판단했습니다.
     */
    private fun observeBlurEffectValue() {
        disposable.add(blurEffectSeekEventPublisher.observeByDebounce(500) { blurValue ->
            _currentImage.value?.let {
                if (it.blur != blurValue) {
                    it.blur = blurValue
                    notifySingleEvent(ImageViewerSingleEventType.ReloadImage(position))
                }
            }
        })
    }

    private fun observeGrayscaleEffectValue() {
        disposable.add(grayscaleSwitchEventPublisher.observeByDebounce(500) { onEffect ->
            _currentImage.value?.let {
                if (it.grayscale != onEffect) {
                    it.grayscale = onEffect
                    notifySingleEvent(ImageViewerSingleEventType.ReloadImage(position))
                }
            }
        })
    }

    fun onChangePage(position: Int) {
        this.position = position
        _currentImage.value = this.imageViewDataList.value!![position]
    }

    fun onChangeBlurEffect(blurValue: Int) {
        blurEffectSeekEventPublisher.onNext(blurValue)
    }

    fun onChangeGrayscaleEffect(onEffect: Boolean) {
        grayscaleSwitchEventPublisher.onNext(onEffect)
    }

    fun onClickMoreButton() {
        _currentImage.value?.let {
            notifySingleEvent(ImageViewerSingleEventType.ClickMoreButton(it.externalLinkUrl))
        }
    }

    fun onClickCloseButton() {
        notifySingleEvent(ImageViewerSingleEventType.ClickCloseButton)
    }

    override fun onSingleTabImage() {
        _functionBarToggler.value = !_functionBarToggler.value!!
    }

    override fun onClickReloadImageAtErrorView() {
        notifySingleEvent(ImageViewerSingleEventType.ReloadImage(position))
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}

/**
 * ViewPager2 Item(RecyclerView Item) 으로 정의된 개별 이미지 화면의 뷰 이벤트
 */
interface ImageViewerSinglePageListener {
    fun onSingleTabImage()
    fun onClickReloadImageAtErrorView()
}

sealed class ImageViewerSingleEventType : SingleEventType {
    object ClickCloseButton : ImageViewerSingleEventType()
    data class ClickMoreButton(val url: String) : ImageViewerSingleEventType()
    data class ReloadImage(val page: Int) : ImageViewerSingleEventType()
}
