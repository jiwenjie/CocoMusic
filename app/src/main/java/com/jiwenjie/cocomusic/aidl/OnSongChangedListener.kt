package com.jiwenjie.cocomusic.aidl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:用户主动切换歌曲时回调，包含如下几种情况
 *  1 播放相同播放列表中的指定歌曲
 *  2 切换到 前一首
 *  3 切换到 后一首
 *  4 切换播放列表
 *  version:1.0
 */
abstract class OnSongChangedListener : IOnSongChangedListener.Stub() {

    /**
     * 该方法在服务端线程的 Binder 线程池中运行，客户端调用时不能操作 UI 控件
     */
    abstract override fun onSongChange(whitch: Song?, index: Int, isNext: Boolean)
}

























