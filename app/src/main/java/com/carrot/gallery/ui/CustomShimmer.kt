package com.carrot.gallery.ui

import android.graphics.Color
import com.facebook.shimmer.Shimmer

/**
 * Created by kyunghoon on 2021-08-31
 */
object CustomShimmer {

    val galleryCustomShimmer: Shimmer = Shimmer.ColorHighlightBuilder()
        .setBaseColor(Color.GRAY)
        .setHighlightColor(Color.WHITE)
        .build()

    val imageViewerCustomShimmer: Shimmer = Shimmer.ColorHighlightBuilder()
        .setBaseColor(Color.GRAY)
        .setHighlightColor(Color.WHITE)
        .build()

}