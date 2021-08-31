package com.carrot.gallery.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by kyunghoon on 2021-08-28
 */
abstract class GridLoadMoreListener(
) : RecyclerView.OnScrollListener() {

    private val threshold = 10

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (recyclerView.layoutManager is GridLayoutManager) {
            val lm = recyclerView.layoutManager as GridLayoutManager
            if (lm.itemCount - lm.findLastVisibleItemPosition() < threshold) {
                onLoadMore()
            }
        }
    }

    abstract fun onLoadMore()
}