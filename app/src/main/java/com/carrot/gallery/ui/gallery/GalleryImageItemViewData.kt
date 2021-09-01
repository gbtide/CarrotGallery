package com.carrot.gallery.data

import com.carrot.gallery.core.util.CollectionUtils
import com.carrot.gallery.model.domain.Image
import okhttp3.internal.toImmutableList

/**
 * Created by kyunghoon on 2021-09-01
 *
 * [ 고민 포인트 ]
 *
 * Gallery 와 ImageViewer 는 별개의 화면이다.
 * 각 화면의 요구사항이 다르므로 각각 ViewData 를 세팅해야한다.
 *
 * 한편 각 화면이 리스트를 공유하고
 * 또한 각 화면에서 동적으로 로딩을 해야하는 상황이다.
 *
 * 만약 List<domain model> 만 공유할 경우
 * 매번
 * Gallery 클릭 -> ImageViewer
 * ImageViewer 백 프래스 -> Gallery
 *
 * 마다 컨버팅을 해줘야하는 이슈가 있다.
 *
 *
 * [ 결정 ]
 * 성능 vs 유연함
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