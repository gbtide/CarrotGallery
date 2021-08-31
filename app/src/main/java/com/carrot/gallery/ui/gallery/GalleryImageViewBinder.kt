package com.carrot.gallery.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.R
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.ItemGalleryImageBinding

/**
 * Created by kyunghoon on 2021-08-28
 */
data class GalleryImage(
    val id: Long,
    val imageUrl: String
)

class GalleryImageViewBinder(
    private val galleryItemClickListener: GalleryItemClickListener,
    private val imageUrlMaker: ImageUrlMaker
) : GalleryItemViewBinder<GalleryImage, GalleryViewHolder>(GalleryImage::class.java) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return GalleryViewHolder(
            ItemGalleryImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), galleryItemClickListener, imageUrlMaker
        )
    }

    override fun bindViewHolder(model: GalleryImage, viewHolder: GalleryViewHolder) =
        viewHolder.bind(model)

    override fun getItemType(): Int = R.layout.item_gallery_image

    override fun areItemsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean =
        (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean =
        (oldItem == newItem)

}

class GalleryViewHolder(
    private val binding: ItemGalleryImageBinding,
    private val galleryItemClickListener: GalleryItemClickListener,
    private val imageUrlMaker: ImageUrlMaker
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(galleryImage: GalleryImage) {
        binding.image = galleryImage
        binding.galleryColumnCount = GalleryCons.COLUMN_COUNT // 개선 포인트
        binding.eventListener = galleryItemClickListener
        binding.galleryThumbnailUrlMaker = imageUrlMaker
        binding.executePendingBindings()
    }

}