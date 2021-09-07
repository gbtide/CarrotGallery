package com.carrot.gallery.ui.viewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.R
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.databinding.ItemImageViewerSimpleImageBinding
import com.carrot.gallery.ui.BaseItemBinder

/**
 * Created by kyunghoon on 2021-08-28
 */
class ImageViewerSimpleImageItemBinder(
    private val itemSinglePageListener: ImageViewerSinglePageListener,
    private val imageUrlMaker: ImageUrlMaker
) : BaseItemBinder<ImageViewerViewData, SimpleImageViewHolder>(
    ImageViewerViewData::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return SimpleImageViewHolder(
            ItemImageViewerSimpleImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemSinglePageListener,
            imageUrlMaker
        )
    }

    override fun bindViewHolder(model: ImageViewerViewData, viewHolder: SimpleImageViewHolder) = viewHolder.bind(model)

    override fun getItemType(): Int = R.layout.item_image_viewer_simple_image

    override fun areItemsTheSame(oldItem: ImageViewerViewData, newItem: ImageViewerViewData): Boolean = (oldItem.id == newItem.id)

    override fun areContentsTheSame(oldItem: ImageViewerViewData, newItem: ImageViewerViewData): Boolean = (oldItem == newItem)
}

class SimpleImageViewHolder(
    private val binding: ItemImageViewerSimpleImageBinding,
    private val singlePageListener: ImageViewerSinglePageListener,
    private val imageUrlMaker: ImageUrlMaker,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewData: ImageViewerViewData) {
        binding.viewData = viewData
        binding.urlMaker = imageUrlMaker
        binding.listener = singlePageListener
        binding.executePendingBindings()
    }
}
