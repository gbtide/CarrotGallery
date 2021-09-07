package com.carrot.gallery.core.di

import com.carrot.gallery.core.OkHttpInterceptor
import com.carrot.gallery.core.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by kyunghoon on 2021-08-27
 */
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

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

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(OkHttpInterceptor())
                    .build()
            )
            .build()
    }
}
