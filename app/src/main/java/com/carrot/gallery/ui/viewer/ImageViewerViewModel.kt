package com.carrot.gallery.ui.viewer

import androidx.lifecycle.*
import com.carrot.gallery.core.event.SingleEventType
import com.carrot.gallery.core.event.ViewModelSingleEventsDelegate
import com.carrot.gallery.core.util.observeByDebounce
import com.carrot.gallery.model.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject


/**
 * Created by kyunghoon on 2021-01-10
 */
@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val singleEventDelegate: ViewModelSingleEventsDelegate,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ImageViewerSinglePageListener, ViewModelSingleEventsDelegate by singleEventDelegate {

    private val images = MutableLiveData<List<Image>>()
    private var oldImages: List<Image>? = null
    private val imageDiff = MutableLiveData<List<Image>>()

    private val imagesObserver = Observer<List<Image>> { _images ->
        val oldSize = oldImages?.size ?: 0
        val diff = if (_images.size > oldSize) _images.subList(oldSize, _images.size) else emptyList()
        oldImages = _images
        imageDiff.value = diff
    }

    val imageViewDataList = imageDiff.map { diff ->
        return@map sumAtImageViewDataList(diff)
    }

    private val position = MutableLiveData<Int>()

    private val positionObserver = Observer<Int> { _position ->
        this.imageViewDataList.value?.let {
            _currentImage.value = it[_position]
        }
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

        images.observeForever(imagesObserver)
        position.observeForever(positionObserver)

        observeBlurEffectValue()
        observeGrayscaleEffectValue()
    }

    fun onInitImages(images: List<Image>) {
        this.images.value = images
    }

    private fun sumAtImageViewDataList(diff: List<Image>): List<ImageViewerViewData> {
        val oldList = imageViewDataList.value ?: emptyList()
        return oldList + ImageViewerViewDataMapper.toImageViewerViewDataList(diff)
    }

    private fun observeBlurEffectValue() {
        disposable.add(blurEffectSeekEventPublisher.observeByDebounce(500) { blurValue ->
            _currentImage.value?.let { it ->
                if (it.blur != blurValue) {
                    it.blur = blurValue
                    reloadCurrentImage()
                }
            }
        })
    }

    private fun observeGrayscaleEffectValue() {
        disposable.add(grayscaleSwitchEventPublisher.observeByDebounce(500) { onEffect ->
            _currentImage.value?.let { it ->
                if (it.grayscale != onEffect) {
                    it.grayscale = onEffect
                    reloadCurrentImage()
                }
            }
        })
    }

    private fun reloadCurrentImage() {
        position.value?.let { pos ->
            notifySingleEvent(ImageViewerSingleEventType.ReloadImage(pos))
        }
    }

    fun onPageSelected(position: Int) {
        this.position.value = position
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
        reloadCurrentImage()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        images.removeObserver(imagesObserver)
        position.removeObserver(positionObserver)
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
