package com.carrot.gallery.model.gallery

import com.google.gson.annotations.SerializedName

/**
 * Created by kyunghoon on 2021-08
 */
data class Image(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,

    @SerializedName("download_url")
    val downloadUrl: String

)