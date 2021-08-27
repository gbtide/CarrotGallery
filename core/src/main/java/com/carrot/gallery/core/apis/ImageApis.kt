package com.carrot.gallery.core.apis

import com.carrot.gallery.model.gallery.Image
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by kyunghoon on 2021-08-27
 */
interface ImageApis {

    @GET("/v2/list")
    suspend fun getImages(@Query("page") page: Int, @Query("limit") limit: Int): List<Image>

    @GET("/id/{id}/info")
    suspend fun getImage(@Path("id") id: Int): Image

}