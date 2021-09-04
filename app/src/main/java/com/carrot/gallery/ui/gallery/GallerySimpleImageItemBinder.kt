package com.carrot.gallery.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.R
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.ItemGallerySimpleImageBinding
import com.carrot.gallery.ui.BaseItemBinder

/**
 * Created by kyunghoon on 2021-08-28
 */
class GallerySimpleImageItemBinder(
    private val galleryItemClickListener: GalleryItemClickListener,
    private val imageUrlMaker: ImageUrlMaker
) : BaseItemBinder<GalleryImageItemViewData.SimpleImage, SimpleImageViewHolder>(
    GalleryImageItemViewData.SimpleImage::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return SimpleImageViewHolder(
            ItemGallerySimpleImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), galleryItemClickListener, imageUrlMaker
        )
    }

    override fun bindViewHolder(model: GalleryImageItemViewData.SimpleImage, viewHolder: SimpleImageViewHolder) = viewHolder.bind(model, viewHolder.adapterPosition)

    override fun getItemType(): Int = R.layout.item_gallery_simple_image

    override fun areItemsTheSame(oldItem: GalleryImageItemViewData.SimpleImage, newItem: GalleryImageItemViewData.SimpleImage): Boolean = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: GalleryImageItemViewData.SimpleImage, newItem: GalleryImageItemViewData.SimpleImage): Boolean = (oldItem == newItem)

}

class SimpleImageViewHolder(
    private val binding: ItemGallerySimpleImageBinding,
    private val galleryItemClickListener: GalleryItemClickListener,
    private val imageUrlMaker: ImageUrlMaker
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(image: GalleryImageItemViewData.SimpleImage, position: Int) {
        binding.image = image
        binding.position = position
        binding.galleryColumnCount = GalleryCons.COLUMN_COUNT // 개선 포인트
        binding.eventListener = galleryItemClickListener
        binding.galleryThumbnailUrlMaker = imageUrlMaker
        binding.executePendingBindings()
    }

}