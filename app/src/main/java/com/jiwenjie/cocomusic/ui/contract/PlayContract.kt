package com.jiwenjie.cocomusic.ui.contract

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.ui.contract.base.BaseView

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/09
 *  desc:
 *  version:1.0
 */
interface PlayContract {

   interface View : BaseView {

      fun setPlayingBitmap(albumArt: Bitmap?)

      fun setPlayingBg(albumArt: Drawable?, isInit: Boolean? = false)

//        fun setPalette(palette: Palette?)

      fun showLyric(lyric: String?, init: Boolean)

      fun updatePlayStatus(isPlaying: Boolean)

      fun updatePlayMode()

      fun updateProgress(progress: Long, max: Long)

      fun showNowPlaying(music: Music?)
   }

   interface Presenter {

      fun updateNowPlaying(music: Music?, isInit: Boolean? = false)
   }
}
