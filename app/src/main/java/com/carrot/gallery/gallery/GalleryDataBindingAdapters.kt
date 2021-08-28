package com.carrot.gallery.gallery

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.carrot.gallery.util.ScreenUtility

/**
 * Created by kyunghoon on 2021-08-28
 */

@BindingAdapter(value = ["galleryImageUrl", "galleryColumnCount"], requireAll = true)
fun loadImageAdjustDeviceSize(
    imageView: ImageView,
    galleryImageUrl: String,
    galleryColumnCount: Int
) {
    val newWith = (ScreenUtility.getScreenWidth(imageView.context) / galleryColumnCount)
    val resizedUrl = "$galleryImageUrl/$newWith/$newWith"
//    Timber.d("### resizedUrl : %s", resizedUrl);

    Glide.with(imageView.context)
        .load(resizedUrl)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade(100))
        .into(imageView)
}
