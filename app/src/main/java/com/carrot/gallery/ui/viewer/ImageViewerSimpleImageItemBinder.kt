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
    private val itemSinglePageListener: ImageViewerSinglePageListener,
    private val imageUrlMaker: ImageUrlMaker
) : BaseItemBinder<ImageViewerViewData, SimpleImageViewHolder>(
    ImageViewerViewData::class.java
) {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return SimpleImageViewHolder(
            ItemImageViewerSimpleImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), itemSinglePageListener, imageUrlMaker
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
        /**
         * [ ViewPager2 기본 스펙 테스트 ]
         *
         * - 최대 5개의 뷰홀더를 생성하며, 4개의 페이지를 유지한다.
         * - notifyDataChanged 를 콜하니 보여지는 페이지만 Re-Binding 이 되나, 페이지 넘기면 나머지 페이지도 모두 Re-Binding 된다.
         * - 예 : 3 (4) 5 6 에서 notifyDataChange 를 거니 (4) 만 Re-Binding 되나, 우로 페이징하면 5,6도 다시 Re-Binding 된다.
         * - 페이징 시 목적지 페이지 기준 좌우 1page 는 미리 생성(or 유지)하고 있다.
         * - 결국 현재 페이지만 Re-load 하고 싶으면 ObserverField 를 쓰거나, notifyItemChanged(page) 를 쓰도록 하자.
         */

        binding.viewData = viewData
        binding.urlMaker = imageUrlMaker
        binding.listener = singlePageListener
        binding.executePendingBindings()
    }

}