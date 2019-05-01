package com.jiwenjie.cocomusic.one.interfaces

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:
 *  version:1.0
 */
interface OnUpdateStatusChanged {
    fun onCompleted()

    fun onStart()

    fun onError(e: Throwable)
}