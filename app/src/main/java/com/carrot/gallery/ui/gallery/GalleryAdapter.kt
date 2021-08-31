package com.carrot.gallery.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

typealias GalleryItemClass = Class<out Any>

typealias GalleryItemBinder = GalleryItemViewBinder<Any, RecyclerView.ViewHolder>

/**
 * Created by kyunghoon on 2021-08
 */
class GalleryAdapter(
    private val viewBinders: Map<GalleryItemClass, GalleryItemBinder>
) : ListAdapter<Any, RecyclerView.ViewHolder>(GalleryItemDiffCallback(viewBinders)) {

    private val viewTypeToBinders = viewBinders.mapKeys { it.value.getItemType() }

    private fun getViewBinder(viewType: Int): GalleryItemBinder =
        viewTypeToBinders.getValue(viewType)

    override fun getItemViewType(position: Int): Int =
        viewBinders.getValue(super.getItem(position).javaClass).getItemType()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewBinder(viewType).createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return getViewBinder(getItemViewType(position)).bindViewHolder(getItem(position), holder)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        getViewBinder(holder.itemViewType).onViewRecycled(holder)
        super.onViewRecycled(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        getViewBinder(holder.itemViewType).onViewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
    }

}

abstract class GalleryItemViewBinder<M, VH : RecyclerView.ViewHolder>(
    val modelClass: Class<out M>
) : DiffUtil.ItemCallback<M>() {

    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun bindViewHolder(model: M, viewHolder: VH)
    abstract fun getItemType(): Int

    // 필요하면 아래 메소드들 사용한다.
    open fun onViewRecycled(viewHolder: VH) = Unit
    open fun onViewDetachedFromWindow(viewHolder: VH) = Unit
}

internal class GalleryItemDiffCallback(
    private val viewBinders: Map<GalleryItemClass, GalleryItemBinder>
) : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return viewBinders[oldItem::class.java]?.areItemsTheSame(oldItem, newItem) ?: false
    }

    // memo. areItemsTheSame true 리턴 후 콜 됨
    // 공식 주석 : This method is called only if areItemsTheSame(int, int) returns true for these items.
    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return viewBinders[oldItem::class.java]?.areContentsTheSame(oldItem, newItem) ?: false
    }

}