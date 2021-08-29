package com.carrot.gallery.core.data

import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.model.domain.Image
import javax.inject.Inject

/**
 * Created by kyunghoon on 2021-08-27
 */
interface ImageRepository {
    suspend fun getImages(page: Int, limit: Int): List<Image>
    suspend fun getImage(id: Long): Image
}

class LoremPicksumImageRepository @Inject constructor(
    private val imageDataSource: ImageDataSource
) : ImageRepository {

    override suspend fun getImages(page: Int, limit: Int): List<Image> {
        val response = imageDataSource.getImages(page, limit)
        return ImageMapper.fromLoremPicsumImages(response)
    }

    override suspend fun getImage(id: Long): Image {
        val response = imageDataSource.getImage(id)
        return ImageMapper.fromLoremPicsumImage(response)
    }

}