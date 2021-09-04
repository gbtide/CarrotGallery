package com.carrot.gallery.core.di

import com.carrot.gallery.core.data.ImageApis
import com.carrot.gallery.core.data.OkHttpInterceptor
import com.carrot.gallery.core.data.ImageDataSource
import com.carrot.gallery.core.data.ImageRepository
import com.carrot.gallery.core.data.LoremPicksumImageDataSource
import com.carrot.gallery.core.data.LoremPicksumImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
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
    fun provideRetrofit(okHttpClient: OkHttpClient, factory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(factory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor {
        return OkHttpInterceptor()
//        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

}