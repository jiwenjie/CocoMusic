package com.jiwenjie.cocomusic.service

import android.content.Context
import com.jiwenjie.cocomusic.aidl.PlayControlImpl
import com.jiwenjie.cocomusic.manager.MediaManager

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc:
 *  version:1.0
 */
class ServiceInit constructor(context: Context, control: PlayControlImpl,
                              manager: MediaManager) {

    private var context: Context? = null
    private var control: PlayControlImpl? = null
    private var manager: MediaManager? = null

    init {
        this.context = context
        this.control = control
        this.manager = manager
    }

    fun start() {
        initData()
        initPlayList()
        initPlayMode()
        initCurrentSong()
    }

    private fun initData() {

    }

    private fun initPlayList() {

    }

    private fun initPlayMode() {

    }

    private fun initCurrentSong() {

    }
}


















