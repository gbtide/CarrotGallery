package com.carrot.gallery.core.di

import android.content.Context
import com.carrot.gallery.core.image.ImageUrlMaker
import com.carrot.gallery.core.image.LoremPicsumImageUrlMaker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by kyunghoon on 2021-08-29
 */
@InstallIn(SingletonComponent::class)
@Module
class UtilsModule {

    @Provides
    @Singleton
    fun provideThumbnailUrlMaker(@ApplicationContext context: Context): ImageUrlMaker {
        return LoremPicsumImageUrlMaker(context)
    }
}
