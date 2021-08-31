package com.carrot.gallery.model.domain

/**
 * Created by kyunghoon on 2021-08
 */
data class Image(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,

    /**
     * 예시 : "https://picsum.photos/id/0/"
     *
     * api response 와는 달리 원본 이미지 url. size 내용을 담고 있지 않습니다.
     */
    val downloadUrl: String

)