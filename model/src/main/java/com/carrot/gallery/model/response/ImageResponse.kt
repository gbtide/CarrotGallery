package com.carrot.gallery.model.response

import com.google.gson.annotations.SerializedName

/**
 * Created by kyunghoon on 2021-08-29
 *
 * {
 * "id":"0",
 * "author":"Alejandro Escamilla",
 * "width":5616,
 * "height":3744,
 * "url":"https://unsplash.com/photos/yC-Yzbqy7PY",
 * "download_url":"https://picsum.photos/id/0/5616/3744"
 * }
 */
data class ImageResponse(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,

    /**
     * "download_url":"https://picsum.photos/id/0/5616/3744"
     *
     * 사이즈 파라미터가 최대값으로 붙어서 내려옵니다.
     */
    @SerializedName("download_url")
    val downloadUrl: String
)