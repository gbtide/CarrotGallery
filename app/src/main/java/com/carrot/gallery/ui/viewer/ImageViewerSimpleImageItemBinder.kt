package com.carrot.gallery.ui.viewer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.carrot.gallery.R
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.ItemImageViewerSimpleImageBinding
import com.carrot.gallery.ui.BaseItemBinder
import com.carrot.gallery.ui.CustomShimmer

/**
 * Created by kyunghoon on 2021-08-28
 */
class ImageViewerSimpleImageItemBinder(
    private val itemViewEventListener: ImageViewerViewEventListener,
    private val imageUrlMaker: ImageUrlMaker
) : BaseItemBinder<ImageViewerViewData, SimpleImageViewHolder>(
    ImageViewerViewData::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return SimpleImageViewHolder(
            ItemImageViewerSimpleImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemViewEventListener, imageUrlMaker
        )
    }

    override fun bindViewHolder(model: ImageViewerViewData, viewHolder: SimpleImageViewHolder) = viewHolder.bind(model)

    override fun getItemType(): Int = R.layout.item_image_viewer_simple_image

    override fun areItemsTheSame(oldItem: ImageViewerViewData, newItem: ImageViewerViewData): Boolean = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: ImageViewerViewData, newItem: ImageViewerViewData): Boolean = (oldItem == newItem)

}

class SimpleImageViewHolder(
    private val binding: ItemImageViewerSimpleImageBinding,
    private val itemViewEventListener: ImageViewerViewEventListener,
    private val imageUrlMaker: ImageUrlMaker
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(simpleImage: ImageViewerViewData) {
        binding.imageViewerView.setOnPhotoTapListener { _, _, _ -> itemViewEventListener.onSingleTabImageEvent() }

        var imageUrl = imageUrlMaker.addFilterEffectParam(
            imageUrlMaker.addAdjustSizeParam(simpleImage.urlWithoutSize, simpleImage.width, simpleImage.height),
            simpleImage.blur,
            simpleImage.grayscale
        )

        binding.imageViewerView.setImageDrawable(null)
        binding.shimmerViewContainer.visibility = View.VISIBLE
        binding.shimmerViewContainer.setShimmer(CustomShimmer.imageViewerCustomShimmer)
        binding.shimmerViewContainer.startShimmer()

        // memo. blink 이슈로 .into(binding.imageViewerView) 를 쓰지 않았습니다.
        Glide.with(binding.imageViewerView.context)
            .asBitmap()
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                    return true
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }
            })
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.imageViewerView.setImageBitmap(bitmap)
                    binding.shimmerViewContainer.stopShimmer()
                    binding.shimmerViewContainer.visibility = View.GONE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        binding.image = simpleImage
        binding.executePendingBindings()
    }

}