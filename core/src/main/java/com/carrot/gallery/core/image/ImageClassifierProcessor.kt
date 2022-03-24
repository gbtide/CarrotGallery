package com.carrot.gallery.core.image

import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Created by kyunghoon on 2022-02-23
 */
interface ImageClassifierProcessor {
    fun process(image: Image): Single<Result<Image>>
}

class ImageClassifierProcessorImpl @Inject constructor(
    private val imageClassifierLoader: ImageClassifierLoader
) : ImageClassifierProcessor {
    override fun process(image: Image): Single<Result<Image>> {
        return imageClassifierLoader.loadClassifier()
            .flatMap { classifier ->
                when (classifier) {
                    is Classifier.ImageClassifier -> {
                        classifier.doClassify(image)
                    }
                    is Classifier.EmptyClassifier -> {
                        Single.just(Result.Error(IllegalStateException()))
                    }
                }
            }
    }
}
