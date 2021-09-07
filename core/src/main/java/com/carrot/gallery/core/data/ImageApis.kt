package com.carrot.gallery.core.data

import com.carrot.gallery.model.response.ImageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by kyunghoon on 2021-08-27
 */
interface ImageApis {

    @GET("/v2/list")
    suspend fun getImages(@Query("page") page: Int, @Query("limit") limit: Int): List<ImageResponse>

    @GET("/id/{id}/info")
    suspend fun getImage(@Path("id") id: Long): ImageResponse
}
