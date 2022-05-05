package com.carrot.gallery.core.image

import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

/**
 * Created by kyunghoon on 2022-02-23
 */
interface ImageClassifierLoader {
    fun loadClassifier(): Single<Classifier>
}

class ImageClassifierLoaderImpl : ImageClassifierLoader {
    private val classifierRelay = BehaviorRelay.create<Classifier>()

    override fun loadClassifier(): Single<Classifier> {
        return when (val currentValue = classifierRelay.value) {
            is Classifier.ImageClassifier -> Single.just(currentValue)
            else -> {
                RemoteClassifierLoader().apply {
                    successCallback = { classifier ->
                        classifierRelay.accept(classifier)
                    }
                    errorCallback = { e ->
                        Timber.e(e)
                        classifierRelay.accept(Classifier.EmptyClassifier)
                    }
                }.loadAsync()
                Single.fromObservable(classifierRelay.take(1))
            }
        }
    }

    fun loadClassifierV2(): Single<Classifier> = Single.create { emitter ->
        when (val currentValue = classifierRelay.value) {
            is Classifier.ImageClassifier -> emitter.onSuccess(currentValue)
            else -> {
                try {
                    RemoteClassifierLoader().apply {
                        successCallback = { classifier ->
                            emitter.onSuccess(classifier)
                        }
                        errorCallback = { e ->
                            Timber.e(e)
                            emitter.onSuccess(Classifier.EmptyClassifier)
                        }
                    }.loadAsync()
                } catch (e: Throwable) {
                    emitter.onError(e)
                }
            }
        }
    }
}

class RemoteClassifierLoader {
    var successCallback: ((Classifier) -> Unit)? = null
    var errorCallback: ((Throwable) -> Unit)? = null

    fun loadAsync() {
        // some processing..
        successCallback?.invoke(Classifier.ImageClassifier)
    }
}