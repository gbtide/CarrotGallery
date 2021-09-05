package com.carrot.gallery.core.data

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.core.util.toImmutableList
import com.carrot.gallery.model.domain.Image
import com.carrot.gallery.model.response.ImageResponse

/**
 * Created by kyunghoon on 2021-08-29
 *
 * Domain Mapper
 */
class ImageMapper {

    companion object {

        fun fromLoremPicsumImages(images: List<ImageResponse>): List<Image> {
            if (CollectionUtils.isEmpty(images)) {
                return emptyList()
            }
            return images.map { image -> fromLoremPicsumImage(image) }.toImmutableList()
        }

        /**
         * memo.
         * download url 에 size 가 붙어 내려와서 ui 레벨에 영향이 가고 있습니다.
         * size path segment 제거하고,
         * 필요하면 화면에서 알아서 image size를 붙이는 방향으로 수정했습니다.
         */
        fun fromLoremPicsumImage(image: ImageResponse): Image {
            val urlRemovedSize = removeLoremPicsumImageSizeSegment(image.downloadUrl)
            return Image(
                image.id,
                image.author,
                image.width,
                image.height,
                image.url,
                urlRemovedSize
            )
        }

        private fun removeLoremPicsumImageSizeSegment(url: String): String {
            return removeLastSegment(removeLastSegment(url))
        }

        private fun removeLastSegment(url: String): String {
            return url.substring(0, url.lastIndexOf('/'))
        }
    }

}