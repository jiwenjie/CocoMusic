package com.jiwenjie.cocomusic.utils

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:
 *  version:1.0
 */
object CoverLoader {

    interface BitmapCallBack {
        fun showBitmap(bitmap: Bitmap?)
    }

    fun loadImageViewByMusic(mContext: Context, music: Music?, callBack: BitmapCallBack) {
        if (music == null) return
        val url = getCoverUriByMusic(music, false)
        loadBitmap(mContext, url, callBack)
    }

    /**
     * 获取专辑图 url
     * music 音乐
     * isBig 是否是大图
     */
    private fun getCoverUriByMusic(music: Music?, isBig: Boolean): String {
        return if (music!!.coverBig != null && isBig) {
            music.coverBig!!
        } else if (music.coverUri != null) {
            music.coverUri!!
        } else {
            music.coverSmall!!
        }
    }

    /**
     * 返回 bitmap
     */
    fun loadBitmap(mContext: Context?, url: String, callBack: BitmapCallBack?) {
        if (mContext == null) return
        Glide.with(mContext)
            .load(url)
            .asBitmap()
            .error(R.drawable.default_album)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                    if (callBack != null && resource != null) {
                        callBack.showBitmap(resource)
                    }
                }
            })
    }
}
















