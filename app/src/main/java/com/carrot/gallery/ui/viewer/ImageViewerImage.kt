package com.carrot.gallery.ui.viewer

/**
 * Created by kyunghoon on 2021-08-29
 */
data class ImageViewerImage(
    val id: Long,
    val author: String,
    val url: String,
    val downloadUrl: String,
    val width: Int,
    val height: Int
)
