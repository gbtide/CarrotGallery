package com.carrot.gallery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by kyunghoon on 2021-08
 */
@HiltAndroidApp
class AppApplication(): Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

}