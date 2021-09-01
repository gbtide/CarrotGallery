package com.carrot.gallery.ui.viewer

import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
import okhttp3.internal.toImmutableList

data class ImageViewerViewData(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val urlWithoutSize: String,
    val externalLinkUrl: String,
    var blur: Int = ImageCons.BLUR_FILTER_DISABLED,
    var grayscale: Boolean = false
)

class ImageViewerViewDataMapper {

    companion object {
        fun toImageViewerViewDataList(images: List<Image>): List<ImageViewerViewData> {
            if (CollectionUtils.isEmpty(images)) {
                return emptyList()
            }

            return images.map { image -> toImageViewerViewData(image) }.toImmutableList()
        }

        private fun toImageViewerViewData(image: Image): ImageViewerViewData {
            return ImageViewerViewData(image.id, image.author, image.width, image.height, image.urlWithoutSize, image.linkUrl, ImageCons.BLUR_FILTER_DISABLED, false)
        }

    }

}