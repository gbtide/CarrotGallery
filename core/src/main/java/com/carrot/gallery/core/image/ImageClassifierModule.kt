package com.carrot.gallery.core.image

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kyunghoon on 2022-02-23
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ImageClassifierModule {
    @Binds
    @Singleton
    abstract fun provideClassifierLoader(
        imageClassifierLoaderImpl: ImageClassifierLoaderImpl
    ): ImageClassifierLoader

    @Binds
    @Singleton
    abstract fun provideClassifierProcessor(
        imageClassifierProcessorImpl: ImageClassifierProcessorImpl
    ): ImageClassifierProcessor
}