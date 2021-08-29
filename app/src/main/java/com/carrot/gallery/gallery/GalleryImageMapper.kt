package com.carrot.gallery.gallery

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
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
            return GalleryImage(image.id, image.downloadUrl)
        }

    }

}