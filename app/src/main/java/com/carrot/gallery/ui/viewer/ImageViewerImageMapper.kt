package com.carrot.gallery.ui.viewer

import com.carrot.gallery.model.domain.Image

/**
 * Created by kyunghoon on 2021-08-29
 */
class ImageViewerImageMapper {

    companion object {

        fun fromImage(image: Image): ImageViewerImage {
            return ImageViewerImage(
                image.id,
                image.author,
                image.downloadUrl,
                image.width,
                image.height
            )
        }

    }

}