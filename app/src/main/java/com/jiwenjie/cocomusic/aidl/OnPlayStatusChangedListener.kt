package com.jiwenjie.cocomusic.aidl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:用户手动暂停，播放歌曲时回调
 *  version:1.0
 */
abstract class OnPlayStatusChangedListener : IOnPlayStatusChangedListener.Stub() {

    /**
     * 1 自动播放时开始播放曲目时回调
     * 2 继续播放，开始播放时回调
     * 当前开始播放曲目
     * 播放列表下标
     */
    abstract override fun playStart(whitch: Song?, index: Int, status: Int)

    /**
     * 1 自动播放时播放曲目播放完成时回调，一般情况下该方法调用后 playStart(Song, int, int) 会被调用
     * 2 暂停，停止播放时回调
     *
     * 当前播放曲目
     * 播放列表下标
     * 播放状态
     */
    abstract override fun playStop(whitch: Song?, index: Int, status: Int)
}