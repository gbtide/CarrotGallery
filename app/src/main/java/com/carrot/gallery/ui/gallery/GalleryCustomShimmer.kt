package com.carrot.gallery.ui.gallery

import android.graphics.Color
import com.facebook.shimmer.Shimmer

/**
 * Created by kyunghoon on 2021-08-31
 */
object GalleryCustomShimmer {

    val customShimmer: Shimmer = Shimmer.ColorHighlightBuilder()
        .setBaseColor(Color.GRAY)
        .setHighlightColor(Color.WHITE)
        .build()

}