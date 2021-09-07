package com.carrot.gallery.util

import android.view.View
import androidx.databinding.BindingAdapter
import com.carrot.gallery.widget.CustomSwipeRefreshLayout
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView

/**
 * Created by kyunghoon on 2021-08
 */
@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("swipeRefreshColors")
fun setSwipeRefreshColors(swipeRefreshLayout: CustomSwipeRefreshLayout, colorResIds: IntArray) {
    swipeRefreshLayout.setColorSchemeColors(*colorResIds)
}

@BindingAdapter("photoTabListener")
fun setOnPhotoTabListener(photoView: PhotoView, photoTabListener: OnPhotoTapListener) {
    photoView.setOnPhotoTapListener(photoTabListener)
}
