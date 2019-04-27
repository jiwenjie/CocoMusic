package com.jiwenjie.cocomusic.service

import android.app.Service
import com.jiwenjie.cocomusic.manager.MediaManager

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc:
 *  version:1.0
 */
abstract class RootService : Service() {

    protected var mediaManager: MediaManager? = null

//    protected var dbController: DBMusicocoController? = null
//
//    protected var playPreference: PlayPreference
//    protected var appPreference: AppPreference
//    protected var settingPreference: SettingPreference

    override fun onCreate() {
        super.onCreate()
        mediaManager = MediaManager.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}