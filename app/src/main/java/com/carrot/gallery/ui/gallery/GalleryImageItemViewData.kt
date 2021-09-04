package com.carrot.gallery.ui.gallery

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
import okhttp3.internal.toImmutableList

/**
 * Created by kyunghoon on 2021-09-01
 */
sealed class GalleryImageItemViewData {
    data class SimpleImage(
        val id: Long,
        val author: String,
        val width: Int,
        val height: Int,
        val linkUrl: String,
        val urlWithoutSize: String

    ) : GalleryImageItemViewData()
}

class GalleryImageItemViewDataMapper {

    companion object {
        fun toSimpleImages(images: List<Image>): List<GalleryImageItemViewData> {
            if (CollectionUtils.isEmpty(images)) {
                return emptyList()
            }

            return images.map { image -> toSimpleImage(image) }.toImmutableList()
        }

        private fun toSimpleImage(image: Image): GalleryImageItemViewData.SimpleImage {
            return GalleryImageItemViewData.SimpleImage(image.id, image.author, image.width, image.height, image.linkUrl, image.urlWithoutSize)
        }

    }

}