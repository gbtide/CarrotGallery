package com.carrot.gallery.core.data

import com.carrot.gallery.core.apis.ImageApis
import com.carrot.gallery.model.gallery.Image
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
interface ImageDataSource {
    suspend fun getImages(page: Int, limit: Int): List<Image>
    suspend fun getImage(id: Long): Image
}

class LoremPicksumImageDataSource @Inject constructor(
    private val imageApis: ImageApis
) : ImageDataSource {

    override suspend fun getImages(page: Int, limit: Int): List<Image> {
        return imageApis.getImages(page, limit)
    }

    override suspend fun getImage(id: Long): Image {
        return imageApis.getImage(id)
    }

}