package com.carrot.gallery.core.util

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.util.DisplayMetrics
import android.view.WindowManager
import org.jetbrains.annotations.NotNull

/**
 * Created by kyunghoon on 2021-08-28
 */
class ScreenUtility {

    companion object {
        fun getScreenWidth(context: Context?): Int {
            return if (context != null) getDisplayMetrics(context).widthPixels else 0
        }

        fun getScreenHeight(context: Context?): Int {
            return if (context != null) getDisplayMetrics(context).heightPixels else 0
        }

        private fun getDisplayMetrics(@NotNull context: Context): DisplayMetrics {
            val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm
        }
    }
}
