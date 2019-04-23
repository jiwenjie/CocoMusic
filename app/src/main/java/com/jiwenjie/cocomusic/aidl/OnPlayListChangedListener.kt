package com.jiwenjie.cocomusic.aidl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:服务端的播放列表改变时回调
 *  1 改变歌单时回调
 *  2 从歌单中移除歌曲时回调
 *  version:1.0
 */
abstract class OnPlayListChangedListener : IOnPlayListChangedListener.Stub() {

    /**
     * 服务端的播放列表改变时回调
     *
     * 1 当前曲目
     * 2 曲目下标
     * 3 歌单 id
     */
    abstract override fun onPlayListChange(current: Song?, index: Int, id: Int)
}