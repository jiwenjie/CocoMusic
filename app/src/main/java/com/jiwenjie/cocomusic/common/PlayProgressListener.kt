package com.jiwenjie.cocomusic.common

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:播放回调
 *  version:1.0
 */
interface PlayProgressListener {
    fun onProgressUpdate(position: Long, duration: Long)
}