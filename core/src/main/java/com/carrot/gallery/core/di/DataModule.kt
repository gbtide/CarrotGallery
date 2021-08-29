package com.carrot.gallery.core.di

import com.carrot.gallery.core.apis.ImageApis
import com.carrot.gallery.core.data.ImageDataSource
import com.carrot.gallery.core.data.ImageRepository
import com.carrot.gallery.core.data.LoremPicksumImageDataSource
import com.carrot.gallery.core.data.LoremPicksumImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by kyunghoon on 2021-08-27
 */
@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideImageApis(retrofit: Retrofit): ImageApis {
        return retrofit.create(ImageApis::class.java)
    }

    @Singleton
    @Provides
    fun provideImageDataSource(imageApis: ImageApis): ImageDataSource {
        return LoremPicksumImageDataSource(imageApis)
    }

    @Singleton
    @Provides
    fun provideImageRepository(imageDataSource: ImageDataSource): ImageRepository {
        return LoremPicksumImageRepository(imageDataSource)
    }
}