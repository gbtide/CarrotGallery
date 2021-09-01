package com.carrot.gallery.ui.gallery

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.ui.CustomShimmer
import com.facebook.shimmer.ShimmerFrameLayout

/**
 * Created by kyunghoon on 2021-08-28
 */

@BindingAdapter(value = ["galleryImageUrl", "galleryColumnCount", "galleryThumbnailUrlMaker", "galleryShimmerView"], requireAll = true)
fun loadGalleryImageAtGrid(
    imageView: ImageView,
    galleryImageUrl: String,
    galleryColumnCount: Int,
    galleryImageUrlMaker: ImageUrlMaker,
    galleryShimmerView: ShimmerFrameLayout
) {
    val imageUrl = galleryImageUrlMaker.addSquareSizeInGridParam(
        galleryImageUrl,
        galleryColumnCount
    )
    galleryShimmerView.setShimmer(CustomShimmer.galleryCustomShimmer)
    galleryShimmerView.startShimmer()

    Glide.with(imageView.context)
        .load(imageUrl)
        .centerCrop()
        .thumbnail(0.1f)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                galleryShimmerView.stopShimmer()
                return true
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                galleryShimmerView.stopShimmer()
                return false
            }
        })
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .transition(DrawableTransitionOptions.withCrossFade(100))
        .into(imageView)
}
