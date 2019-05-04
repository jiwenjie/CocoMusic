package com.jiwenjie.cocomusic.common

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.jiwenjie.cocomusic.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.ui.MainActivity

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:
 *  version:1.0
 */
object NavigationHelper {

    fun getNowPlayingIntent(context: Context): Intent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = Constants.DEAULT_NOTIFICATION
        return intent
    }

    fun getLyricIntent(context: Context): Intent {
        val intent = Intent(MusicPlayerService.ACTION_LYRIC)
        intent.component = ComponentName(context, MusicPlayerService::class.java)
        return intent
    }
}