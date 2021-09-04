package com.carrot.gallery

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule


/**
 * Created by kyunghoon on 2021-08-31
 *
 */
@GlideModule
class GalleryGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setSourceExecutor(
            GlideExecutor.newSourceBuilder()
                .setThreadCount(12)
                .build()
        )
    }

}