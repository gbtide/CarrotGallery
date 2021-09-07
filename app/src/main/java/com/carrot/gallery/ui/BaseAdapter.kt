package com.carrot.gallery.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * 'submit 된 리스트' 의 각각의 아이템(데이터) 타입
 */
typealias ItemClass = Class<out Any>

/**
 * [BaseItemBinder] 주석 참고
 */
typealias ItemBinder = BaseItemBinder<Any, RecyclerView.ViewHolder>

/**
 * kyunghoon on 2021-08
 *
 * - Google I/O 프로젝트의 FeedAdapter 를 참고해서 일부 네이밍 + 주석만 변경하여 작성했습니다.
 */
class BaseAdapter(
    /**
     * 데이터 타입 -> [ItemBinder]
     */
    private val viewBinders: Map<ItemClass, ItemBinder>

) : ListAdapter<Any, RecyclerView.ViewHolder>(ItemDiffCallback(viewBinders)) {

    /**
     * 뷰타입(view holder's layout resource id) -> 바인더
     */
    private val viewTypeToBinders: Map<Int, ItemBinder> = viewBinders.mapKeys { it.value.getItemType() }

    private fun getViewBinder(viewType: Int): ItemBinder = viewTypeToBinders.getValue(viewType)

    /**
     * submit 된 데이터의 타입이 분류되는 위치입니다.
     * 타입 검색을 HashMap 으로 하기 때문에 검색 시간 복잡도는 O(1)
     *
     * @return [BaseItemBinder.getItemType] 참고
     */
    override fun getItemViewType(position: Int): Int = viewBinders.getValue(super.getItem(position).javaClass).getItemType()

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

/**
 * 실제 ViewHolder 정의 및 생성 방식, Bind 될 데이터 타입과 바인딩 방식 등이 정의됩니다.
 */
abstract class BaseItemBinder<M, VH : RecyclerView.ViewHolder>(
    /**
     * View Holder 에 바인드 될 데이터(리스트 아이템) 타입
     */
    val modelClass: Class<out M>

) : DiffUtil.ItemCallback<M>() {

    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun bindViewHolder(model: M, viewHolder: VH)

    /**
     * 보통 inflate 할 뷰의 layout resource id 를 넣으시면 됩니다.
     */
    abstract fun getItemType(): Int

    open fun onViewRecycled(viewHolder: VH) = Unit
    open fun onViewDetachedFromWindow(viewHolder: VH) = Unit
}

internal class ItemDiffCallback(
    private val viewBinders: Map<ItemClass, ItemBinder>
) : DiffUtil.ItemCallback<Any>() {

    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return viewBinders[oldItem::class.java]?.areItemsTheSame(oldItem, newItem) ?: false
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return viewBinders[oldItem::class.java]?.areContentsTheSame(oldItem, newItem) ?: false
    }
}
