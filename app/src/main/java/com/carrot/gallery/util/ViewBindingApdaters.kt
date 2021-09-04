package com.carrot.gallery.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.carrot.gallery.widget.CustomSwipeRefreshLayout
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by kyunghoon on 2021-08
 */
@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("swipeRefreshColors")
fun setSwipeRefreshColors(swipeRefreshLayout: CustomSwipeRefreshLayout, colorResIds: IntArray) {
    swipeRefreshLayout.setColorSchemeColors(*colorResIds)
}

@BindingAdapter(value = ["glideImage", "glideCenterInside"], requireAll = false)
fun setGlideImage(imageView: ImageView, glideImage: String?, glideCenterInside: Boolean) {
    val builder = Glide.with(imageView.context).load(glideImage)
    if (glideCenterInside) {
        builder.centerInside()
    } else {
        builder.centerCrop()
    }
    builder.diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade(300))
        .into(imageView)
}

@BindingAdapter("photoTabListener")
fun setOnPhotoTabListener(photoView: PhotoView, photoTabListener: OnPhotoTapListener) {
    photoView.setOnPhotoTapListener(photoTabListener)
}