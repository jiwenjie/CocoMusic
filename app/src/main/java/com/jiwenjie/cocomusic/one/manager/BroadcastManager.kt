package com.jiwenjie.cocomusic.one.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc:广播管理类
 *  version:1.0
 */
class BroadcastManager private constructor() {

    companion object {
        // 主界面【主歌单】信息更新（主要针对歌曲收藏状态）
        const val STORM_FILTER_MAIN_SHEET_UPDATE = "storm_filter_main_sheet_update"

        // 主界面【我的歌单】信息更新
        const val STORM_FILTER_MY_SHEET_UPDATE = "storm_filter_main_sheet_update"

        // 歌单详情界面信息更新
        const val STORM_FILTER_SHEET_DETAIL_SONGS_UPDATE = "storm_filter_detail_songs_update"

        // 服务器退出
        const val STORM_FILTER_PLAY_SERVICE_QUIT = "storm_filter_play_service_quit"

        // 播放界面界面主题改变
        const val STORM_FILTER_PLAY_UI_MODE_CHANGE = "storm_filter_play_ui_mode_change"

        object Play {
            const val STORM_PLAY_THEME_CHANGE_TOKEN = "storm_play_theme_change_token"
            const val STORM_PLAY_APP_THEME_CHANGE = 1
            const val STORM_PLAY_PLAY_THEME_CHANGE = 2
        }

        // 定时关闭应用时在主界面左边导航栏显示倒计时
        const val STORM_FILTER_APP_QUIT_TIME_COUNTDOWN = "storm_filter_app_quit_time_countdown"

        object Countdown {
            const val STORM_APP_QUIT_TIME_COUNTDOWN_STATUS = "storm_app_quit_time_countdown_status"
            const val STORM_START_COUNTDOWN = 1
            const val STORM_STOP_COUNTDOWN = 2
        }

        // 应用主题自动切换
        const val STORM_FILTER_APP_THEME_CHANGE_AUTOMATIC = "storm_filter_app_theme_change_automatic"
        const val STORM_APP_THEME_CHANGE_AUTOMIC_TOKEN = "storm_filter_app_theme_change_automatic_token"
        const val APP_THEME_CHANGE_AUTOMATIC_WHITE = 1
        const val APP_THEME_CHANGE_AUTOMATIC_DARK = 2

        // 耳机插入和拔出事件
        const val STORM_FILTER_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG"

        private var mInstance: BroadcastManager? = null

        fun getInstance(): BroadcastManager {
            if (mInstance == null) {
                mInstance =
                        BroadcastManager()
            }
            return mInstance!!
        }
    }

    /**
     * 注册广播接收器
     */
    fun registerBroadReceiver(context: Context, receiver: BroadcastReceiver, identity: String) {
        val filter = IntentFilter(identity)
        context.registerReceiver(receiver, filter)
    }

    /**
     * 发送广播
     */
    fun sendBroadcast(context: Context, identity: String, extras: Bundle?) {
        val intent = Intent()
        if (extras != null) {
            intent.putExtras(extras)
        }
        intent.action = identity
        context.sendBroadcast(intent)
    }

    /**
     * 注销广播接收者
     */
    fun unregisterReceiver(context: Context, receiver: BroadcastReceiver) {
        context.unregisterReceiver(receiver)
    }
}











