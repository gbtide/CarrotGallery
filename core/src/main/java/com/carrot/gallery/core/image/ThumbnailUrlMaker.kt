package com.carrot.gallery.core.image

import android.content.Context
import com.carrot.gallery.core.util.ScreenUtility

/**
 * Created by kyunghoon on 2021-08-29
 */
interface ThumbnailUrlMaker {

    /**
     * 현재 디바이스에 맞는 사이즈를 불러옵니다.
     */
    fun makeUrlAdjustDevice(url: String, originWidth: Int, originHeight: Int): String

    /**
     * 현재 디바이스 + 그리드 열숫자에 맞는 정사각형 사이즈를 불러옵니다.
     */
    fun makeSquareImageUrlAdjustDevice(url: String, columnCount: Int): String
}

class LoremPicsumThumbnailUrlMaker constructor(
    val context: Context
) : ThumbnailUrlMaker {

    override fun makeUrlAdjustDevice(
        url: String,
        originWidth: Int,
        originHeight: Int
    ): String {
        val newWith = ScreenUtility.getScreenWidth(context)
        val newHeight =
            (newWith.toFloat() * (originHeight.toFloat() / originWidth.toFloat())).toInt()
        return "$url/$newWith/$newHeight"
    }

    override fun makeSquareImageUrlAdjustDevice(url: String, columnCount: Int): String {
        val newWith = (ScreenUtility.getScreenWidth(context) / columnCount)
        return "$url/$newWith/$newWith"
    }

}