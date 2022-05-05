package com.carrot.gallery.core.image

import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import io.reactivex.rxjava3.core.Single

/**
 * Created by kyunghoon on 2022-02-23
 */
sealed class Classifier {
    object ImageClassifier : Classifier() {
        fun doClassify(image: Image): Single<Result<Image>> {
            return Single.just(
                Result.Success(
                    image.copy(linkUrl = "https://classify.images.com?uri=" + image.linkUrl)
                )
            )
        }
    }

    object EmptyClassifier : Classifier()
}