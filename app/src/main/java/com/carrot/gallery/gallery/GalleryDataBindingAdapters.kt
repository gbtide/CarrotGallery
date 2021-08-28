package com.carrot.gallery.gallery

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.carrot.gallery.util.ScreenUtility
import timber.log.Timber

/**
 * Created by kyunghoon on 2021-08-28
 */

@BindingAdapter(value = ["galleryImageUrl", "galleryColumnCount"], requireAll = true)
fun loadResizedImage(imageView: ImageView, galleryImageUrl: String, galleryColumnCount: Int) {
    val newWith = ScreenUtility.getScreenWidth(imageView.context) / galleryColumnCount
    val resizedUrl = "$galleryImageUrl/$newWith/$newWith"
    Timber.d("### resizedUrl : %s", resizedUrl);

    val builder = Glide.with(imageView.context).load(resizedUrl)
    builder.centerCrop()
    builder.diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade(300))
        .into(imageView)
}
