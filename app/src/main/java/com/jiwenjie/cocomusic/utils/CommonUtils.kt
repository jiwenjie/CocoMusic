package com.jiwenjie.cocomusic.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/05
 *  desc:一些公共的工具方法
 *  version:1.0
 */
object CommonUtils {

    /**
     * @param inSampleSize 图片像素的 1/n*n
     */
    fun createBlurredImageFromBitmap(bitmap: Bitmap, inSampleSize: Int): Drawable {
        val rs = RenderScript.create(CocoApp.contextInstance)
        val options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        val imageInByte = stream.toByteArray()
        val bis = ByteArrayInputStream(imageInByte)
        val blurTemplate = BitmapFactory.decodeStream(bis, null, options)

        val input = Allocation.createFromBitmap(rs, blurTemplate)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(10f)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(blurTemplate)

        return BitmapDrawable(CocoApp.contextInstance.resources, blurTemplate)
    }

    /**
     * 音乐名格式化
     */
    fun getTitle(title: String?): String {
        var strTitle = stringFilter(title)
        if (strTitle.isNullOrEmpty()) strTitle = CocoApp.contextInstance.getString(R.string.unknown)

        return strTitle!!
    }

    /**
     * 歌手专辑格式化
     */
    fun getArtistAndAlbum(artist: String, album: String): String {
        val strArt = stringFilter(artist)
        val strAlbum = stringFilter(album)

        return if (strArt.isNullOrEmpty() && strAlbum.isNullOrEmpty()) ""
        else if (!strArt.isNullOrEmpty() && strAlbum.isNullOrEmpty()) strArt
        else if (strArt.isNullOrEmpty() && !strAlbum.isNullOrEmpty()) album
        else strArt + strAlbum
    }

    /**
     * 过滤特殊字符
     */
    fun stringFilter(str: String?): String? {
        if (str.isNullOrEmpty()) return null

        val regEx = "<[^>]+>"
        val pattern = Pattern.compile(regEx)
        val matcher = pattern.matcher(str)
        return matcher.replaceAll("").trim()
    }

}
















