package com.carrot.gallery.model.domain

/**
 * Created by kyunghoon on 2022-02-23
 */
sealed class ImageUploadState {
    object Uploading : ImageUploadState()
    data class Complete(val imagePath: String) : ImageUploadState()
    data class Error(val error: Throwable) : ImageUploadState()
}