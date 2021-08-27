package com.carrot.gallery.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.carrot.gallery.BR
import com.carrot.gallery.databinding.ItemImageBinding
import com.carrot.gallery.model.gallery.Image

/**
 * Created by kyunghoon on 2021-08
 */
class ImageListAdapter(private val imageClickListener: ImageClickListener) :
    ListAdapter<Image, ImageItemViewHolder>(MainDiff) {

    companion object {
        const val COLUMN_COUNT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        return ImageViewHolder(ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
            .apply {
                eventListener = imageClickListener
            })
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

//    override fun getItemViewType(position: Int): Int {
//        return getItem(position)?.getType()?.value ?: -1
//    }

}

abstract class ImageItemViewHolder(
    @NonNull itemView: View
) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(galleryItem: Image?)
}

class ImageViewHolder(private val binding: ItemImageBinding) :
    ImageItemViewHolder(binding.root) {

    override fun bind(galleryItem: Image?) {
        galleryItem?.let {
            binding.setVariable(BR.image, it)
            binding.executePendingBindings()
        }
    }
}

object MainDiff : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}