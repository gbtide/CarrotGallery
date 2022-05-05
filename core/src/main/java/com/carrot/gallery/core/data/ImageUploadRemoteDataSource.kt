package com.carrot.gallery.core.data

import com.carrot.gallery.core.image.BitmapExtractor
import com.carrot.gallery.core.image.BitmapExtractorFunction
import com.carrot.gallery.core.image.ImageClassifierProcessor
import com.carrot.gallery.core.result.Result
import com.carrot.gallery.model.domain.Image
import com.carrot.gallery.model.domain.ImageUploadState
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/**
 * Created by kyunghoon on 2022-02-23
 */
interface ImageUploadRemoteDataSource {
    fun state(): Observable<ImageUploadState>
    fun upload(images: List<Image>): Observable<Result<Unit>>
}


class ImageUploadRemoteDataSourceImpl @Inject constructor(
    private val imageClassifierProcessor: ImageClassifierProcessor,
    @BitmapExtractor private val bitmapExtractor: BitmapExtractorFunction
) : ImageUploadRemoteDataSource {

    private val state = PublishRelay.create<ImageUploadState>()

    override fun state(): Observable<ImageUploadState> {
        return state.observeOn(AndroidSchedulers.mainThread())
    }

    override fun upload(images: List<Image>): Observable<Result<Unit>> {
        return Observable.fromIterable(images)
            .concatMapEager { singleImage ->
                val sum2 = bitmapExtractor.invoke(singleImage.linkUrl)
                    .flatMap {
                        Single.just(singleImage)
                    }.flatMap { image ->
                        imageClassifierProcessor.process(image)
                            .flatMap {
                                Single.just(Result.Success(image) as Result<Image>)
                            }
                            .onErrorReturnItem(Result.Error(IllegalStateException()))
                    }.map { result ->
                        when (result) {
                            is Result.Loading -> {
                                state.accept(ImageUploadState.Uploading)
                                Result.Loading
                            }
                            is Result.Success -> {
                                state.accept(ImageUploadState.Complete(result.data.linkUrl))
                                Result.Success(Unit)
                            }
                            is Result.Error -> {
                                state.accept(ImageUploadState.Error(result.exception))
                                Result.Error(IllegalStateException())
                            }
                        }
                    }.toObservable()
//                sum2

                // flatMapObservable flatMapSingle 써보려고 한거니 확인해보자
                // 참고로 flatMapObservable 도, flatMapSingle 도 Single 이 아니라 Observable 을 리턴한다.
                // Single 과 Observalble 은 별도 인터페이스
                // flatMapObservable 은 Single 클래스 내부 함수, 그런데 리턴은 Observable
                // flatMapSingle 은 Observable 클래스 내부 함수 그런데 리턴은 Single이 아니라 Observable
                //
                val sum = bitmapExtractor.invoke(singleImage.linkUrl)
                    .flatMapObservable {
                        // water mark 처리
                        // TODO water mark processor
                        Observable.just(singleImage)
                    }
                    .flatMapSingle { image ->
                        // 여기 복습 잘
                        imageClassifierProcessor.process(image)
                            .flatMap {
                                Single.just(Result.Success(image) as Result<Image>)
                            }
                            .onErrorReturnItem(Result.Error(IllegalStateException()))
                    }.map { result ->
                        when (result) {
                            is Result.Loading -> {
                                state.accept(ImageUploadState.Uploading)
                                Result.Loading
                            }
                            is Result.Success -> {
                                state.accept(ImageUploadState.Complete(result.data.linkUrl))
                                Result.Success(Unit)
                            }
                            is Result.Error -> {
                                state.accept(ImageUploadState.Error(result.exception))
                                Result.Error(IllegalStateException())
                            }
                        }
                    }
                sum
            }
    }
}