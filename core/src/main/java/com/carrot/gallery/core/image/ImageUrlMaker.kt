package com.carrot.gallery.core.image

import android.content.Context
import android.net.Uri
import com.carrot.gallery.core.domain.ImageCons
import com.carrot.gallery.core.util.ScreenUtility

/**
 * Created by kyunghoon on 2021-08-29
 */
interface ImageUrlMaker {

    /**
     * 현재 디바이스에 맞는 사이즈 파라미터를 붙여줍니다
     */
    fun addAdjustSizeParam(url: String, originWidth: Int, originHeight: Int): String

    /**
     * 현재 디바이스 + 그리드 열숫자에 맞는 정사각형 사이즈를 불러오기 위해 파라미터를 붙여줍니다.
     */
    fun addSquareSizeInGridParam(url: String, columnCount: Int): String

    /**
     * 필터 이펙트 파라미터를 붙여줍니다.
     */
    fun addFilterEffectParam(url: String, blur: Int, grayscale: Boolean): String
}

class LoremPicsumImageUrlMaker constructor(
    val context: Context
) : ImageUrlMaker {

    override fun addAdjustSizeParam(
        url: String,
        originWidth: Int,
        originHeight: Int
    ): String {
        val newWith = ScreenUtility.getScreenWidth(context)
        val newHeight =
            (newWith.toFloat() * (originHeight.toFloat() / originWidth.toFloat())).toInt()
        return "$url/$newWith/$newHeight"
    }

    override fun addSquareSizeInGridParam(url: String, columnCount: Int): String {
        val newWith = (ScreenUtility.getScreenWidth(context) / columnCount)
        return "$url/$newWith/$newWith"
    }

    /**
     * https://picsum.photos/id/870/200/300?blur=2
     * https://picsum.photos/id/870/200/300?grayscale
     * https://picsum.photos/id/870/200/300?grayscale&blur=2
     */
    override fun addFilterEffectParam(url: String, blur: Int, grayscale: Boolean): String {
        val uri = Uri.parse(url)
        val builder: Uri.Builder = uri.buildUpon()
        if (blur >= ImageCons.BLUR_FILTER_MIN_VALUE) {
            val finalBlur = if (blur > ImageCons.BLUR_FILTER_MAX_VALUE) ImageCons.BLUR_FILTER_MAX_VALUE else blur
            builder.appendQueryParameter("blur", finalBlur.toString())
        }
        if (grayscale) {
            builder.appendQueryParameter("grayscale", "")
        }
        return builder.toString()
    }
}
