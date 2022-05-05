package com.carrot.gallery.core.image

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by kyunghoon on 2022-02-24
 */
typealias BitmapExtractorFunction = (String) -> Single<Bitmap>

@Module
@InstallIn(SingletonComponent::class)
class ImageModule {

    @BitmapExtractor
    @Singleton
    @Provides
    fun provideBitmapExtractor(
        @ApplicationContext context: Context
    ): BitmapExtractorFunction {
        return { filePath ->
            Single.fromFuture(
                Glide.with(context)
                    .asBitmap()
                    .load(filePath)
                    .centerCrop()
                    .submit()
            ).subscribeOn(Schedulers.io())
        }
    }

}