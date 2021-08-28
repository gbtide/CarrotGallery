package com.carrot.gallery.gallery

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.gallery.Image
import okhttp3.internal.toImmutableList

/**
 * Created by kyunghoon on 2021-08-28
 */
class GalleryImageMapper {

    companion object {

        fun fromImages(images: List<Image>): List<GalleryImage> {
            if (CollectionUtils.isEmpty(images)) {
                return emptyList()
            }
            return images.map { image -> fromImage(image) }.toImmutableList()
        }

        private fun fromImage(image: Image): GalleryImage {
            val urlRemovedSize = removeImageSizeSegment(image.downloadUrl)
            return GalleryImage(image.id, urlRemovedSize)
        }

        private fun removeImageSizeSegment(url: String): String {
            return removeLastSegment(removeLastSegment(url))
        }

        private fun removeLastSegment(url: String): String {
            return url.substring(0, url.lastIndexOf('/'))
        }

    }

}