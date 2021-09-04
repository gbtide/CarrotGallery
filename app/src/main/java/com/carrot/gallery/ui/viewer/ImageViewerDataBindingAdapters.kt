package com.carrot.gallery.ui.viewer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.ui.CustomShimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by kyunghoon on 2021-08-28
 *
 * 고민. 특정 화면의 처리를 담고 있는 로직을 @BindingAdapter 로 처리하는 것이 바람직한지 고민입니다. (처리 방식 비교 - [ImageViewerSimpleImageItemBinder])
 *
 */
@BindingAdapter(value = ["imageViewerViewData", "imageViewerThumbnailUrlMaker", "imageViewerShimmerView", "imageViewerErrorView"], requireAll = true)
fun loadImageToImageViewer (
    photoView: PhotoView,
    imageViewerViewData: ImageViewerViewData,
    imageViewerImageUrlMaker: ImageUrlMaker,
    shimmerViewLayout: ShimmerFrameLayout,
    errorView: View
) {
    // 1. prepare image load
    photoView.setImageDrawable(null)
    errorView.visibility = View.GONE
    shimmerViewLayout.visibility = View.VISIBLE
    shimmerViewLayout.setShimmer(CustomShimmer.imageViewerCustomShimmer)
    shimmerViewLayout.startShimmer()

    val url = imageViewerImageUrlMaker.addFilterEffectParam(
        imageViewerImageUrlMaker.addAdjustSizeParam(imageViewerViewData.urlWithoutSize, imageViewerViewData.width, imageViewerViewData.height),
        imageViewerViewData.blur,
        imageViewerViewData.grayscale
    )

    // 2. load!
    Glide.with(photoView.context)
        .asBitmap()
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                shimmerViewLayout.stopShimmer()
                shimmerViewLayout.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                return true
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                shimmerViewLayout.stopShimmer()
                shimmerViewLayout.visibility = View.GONE
                errorView.visibility = View.GONE
                return false
            }
        })
        // memo. blink 이슈로 .into(binding.imageViewerView) 를 쓰지 않았습니다.
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                photoView.setImageBitmap(bitmap)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
}
