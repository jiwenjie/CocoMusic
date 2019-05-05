package com.jiwenjie.cocomusic.common

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import com.jiwenjie.cocomusic.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.ui.MainActivity
import com.jiwenjie.cocomusic.ui.PlayerDetailActivity

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:导航工具类
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

    /**
     * 跳转到歌曲详情界面
     */
    fun navigateToPlaying(activity: Activity, transitionView: View? = null) {
        val intent = Intent(activity, PlayerDetailActivity::class.java)
        if (transitionView != null) {
            val compat = ActivityOptionsCompat.makeScaleUpAnimation(transitionView,
                    transitionView.width / 2, transitionView.height / 2, 0, 0)
            ActivityCompat.startActivity(activity, intent, compat.toBundle())
        } else {
            activity.startActivity(intent)
        }
    }
}





































