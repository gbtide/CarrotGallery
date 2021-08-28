package com.carrot.gallery.util

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.util.DisplayMetrics
import android.view.WindowManager


/**
 * Created by kyunghoon on 2021-08-28
 */
class ScreenUtility {

    companion object {
        fun getScreenWidth(context: Context): Int {
            val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm.widthPixels
        }

    }

}