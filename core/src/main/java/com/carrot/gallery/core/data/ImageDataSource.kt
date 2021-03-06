package com.carrot.gallery.core.data

import com.carrot.gallery.model.response.ImageResponse
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
interface ImageDataSource {
    suspend fun getImages(page: Int, limit: Int): List<ImageResponse>
    suspend fun getImage(id: Long): ImageResponse
}

class LoremPicksumImageDataSource @Inject constructor(
    private val imageApis: ImageApis
) : ImageDataSource {

    override suspend fun getImages(page: Int, limit: Int): List<ImageResponse> {
        return imageApis.getImages(page, limit)
    }

    override suspend fun getImage(id: Long): ImageResponse {
        return imageApis.getImage(id)
    }
}
