package com.carrot.gallery.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.carrot.gallery.widget.CustomSwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.util.*

/**
 * Created by kyunghoon on 2021-08
 */
@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("swipeRefreshColors")
fun setSwipeRefreshColors(swipeRefreshLayout: CustomSwipeRefreshLayout, colorResIds: IntArray) {
    // memo.
    // https://kotlinlang.org/docs/reference/functions.html#variable-number-of-arguments-varargs
    //
    // if we already have an array and want to pass its contents to the function, we use the spread operator (prefix the array with *):
    //
    // val a = arrayOf(1, 2, 3)
    // val list = asList(-1, 0, *a, 4)
    swipeRefreshLayout.setColorSchemeColors(*colorResIds)
}

@BindingAdapter(value = ["glideImage", "glideCenterInside"], requireAll = false)
fun setGlideImage(imageView: ImageView, glideImage: String?, glideCenterInside: Boolean) {
    val builder = Glide.with(imageView.context).load(glideImage)
    if (glideCenterInside) {
        builder.centerInside()
    } else {
        // default
        builder.centerCrop()
    }
    builder.diskCacheStrategy(DiskCacheStrategy.NONE)
        .transition(DrawableTransitionOptions.withCrossFade(300))
        .into(imageView)
}

@BindingAdapter("image")
fun setImage(imageView: SubsamplingScaleImageView, localPath: String) {
    imageView.setImage(ImageSource.uri(localPath))
}

@BindingAdapter(value = ["formatSeconds", "pattern"])
fun secondsToDateText(textView: TextView, formatSeconds: Long, pattern: String) {
    textView.text = TimeUtils.parseToHHmmss(formatSeconds)
}