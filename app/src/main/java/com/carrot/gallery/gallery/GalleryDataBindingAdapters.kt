package com.carrot.gallery.gallery

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.carrot.gallery.core.image.ImageUrlMaker

/**
 * Created by kyunghoon on 2021-08-28
 */

@BindingAdapter(value = ["galleryImageUrl", "galleryColumnCount", "galleryThumbnailUrlMaker"], requireAll = true)
fun loadGalleryImageAtGrid(
    imageView: ImageView,
    galleryImageUrl: String,
    galleryColumnCount: Int,
    galleryImageUrlMaker: ImageUrlMaker
) {
    val imageUrl = galleryImageUrlMaker.addSquareSizeInGridParam(
        galleryImageUrl,
        galleryColumnCount
    )
//    Timber.d("### resizedUrl : %s", imageUrl)
    Glide.with(imageView.context)
        .load(imageUrl)
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade(100))
        .into(imageView)
}
