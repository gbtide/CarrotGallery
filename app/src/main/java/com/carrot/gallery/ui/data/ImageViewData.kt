package com.carrot.gallery.ui.data

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
import okhttp3.internal.toImmutableList

/**
 * Created by kyunghoon on 2021-09-01
 */
interface ImageViewData

data class SimpleImage(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String

) : ImageViewData

class ImageViewDataMapper {

    companion object {
        fun toSimpleImages(images: List<Image>): List<SimpleImage> {
            if (CollectionUtils.isEmpty(images)) {
                return emptyList()
            }
            return images.map { image -> toSimpleImage(image) }.toImmutableList()
        }

        private fun toSimpleImage(image: Image): SimpleImage {
            return SimpleImage(image.id, image.author, image.width, image.height, image.url, image.downloadUrl)
        }

    }

}