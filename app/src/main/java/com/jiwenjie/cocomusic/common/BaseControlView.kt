package com.jiwenjie.cocomusic.common

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.jiwenjie.cocomusic.aidl.Music

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/04
 *  desc:
 *  version:1.0
 */
interface BaseControlView {

    fun setPlayingBitmap(albumArt: Bitmap?)

    fun setPlayingBg(albumArt: Drawable?, isInit: Boolean? = false)

    fun showLyric(lyric: String?, init: Boolean)

    fun updatePlayStatus(isPlaying: Boolean)

    fun updatePlayMode()

    fun updateProgress(progress: Long, max: Long)

    fun showNowPlaying(music: Music?)
}






























