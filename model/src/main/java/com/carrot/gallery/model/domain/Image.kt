package com.carrot.gallery.model.domain

/**
 * Created by kyunghoon on 2021-08
 */
data class Image(
    val id: Long,
    val author: String,
    val width: Int,
    val height: Int,
    val linkUrl: String,

    /**
     * 예시 : "https://picsum.photos/id/0/"
     */
    val urlWithoutSize: String

)