package com.carrot.gallery

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.module.AppGlideModule


/**
 * Created by kyunghoon on 2021-08-31
 *
 * memo.
 * core 보다는 각 feature 에서 정의하는 것이 좋을 것 같아 feautre (ui)모듈 레벨에 정의했습니다.
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