package com.jiwenjie.cocomusic.aidl

import android.content.Context
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.jiwenjie.cocomusic.one.service.AudioFocusManager
import com.jiwenjie.cocomusic.one.service.MediaSessionManager
import com.jiwenjie.cocomusic.one.service.PlayController

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:仅有从 IPlayControl.aidl 继承的方法在跨进程调用时有效
 *  1 该类中的方法运行在服务端 Binder 线程池中，所以需要处理线程同步
 *  2 这些方法被客户端调用的时候客户端线程会被挂起，如果客户端的线程为 UI 线程，注意处理耗时操作以避免出现的 ANR
 *  该实现类不再抛出 RemoteException 异常
 *  version:1.0
 */
open class PlayControlImpl(context: Context): IPlayControl.Stub() {

    protected val mSongChangeListeners: RemoteCallbackList<IOnSongChangedListener>
    protected val mStatusChangeListeners: RemoteCallbackList<IOnPlayStatusChangedListener>
    protected val mPlayListChangeListeners: RemoteCallbackList<IOnPlayListChangedListener>
    protected val mDataIsReadyListeners: RemoteCallbackList<IOnDataIsReadyListener>

    private val manager: PlayController
    private val focusManager: AudioFocusManager?
    private val sessionManager: MediaSessionManager

    private val context: Context = context

    init {
        this.mSongChangeListeners = RemoteCallbackList()
        this.mStatusChangeListeners = RemoteCallbackList()
        this.mPlayListChangeListeners = RemoteCallbackList()
        this.mDataIsReadyListeners = RemoteCallbackList()

        this.sessionManager = MediaSessionManager(context, this)
        this.focusManager = AudioFocusManager(context, this)
        this.manager = PlayController.getMediaContController(
            context,
            focusManager,
            sessionManager,
            NotifyStatusChange(),
            NotifySongChange(),
            NotifyPlayListChange()
        )
    }

    /**
     * 播放相同列表中的指定曲目
     *
     * whitch 曲目
     * return 播放是否成功
     */
    @Synchronized
    override fun play(whitch: Song?): Int {
        if (whitch == null) return -1
        var re = PlayController.ERROR_UNKNOWN
        if (manager.getCurrentSong() != whitch) {
            re = manager.play(whitch)
        }
        return re
    }

    override fun playByIndex(index: Int): Int {
        var re = PlayController.ERROR_UNKNOWN
        if (index < manager.getSongList().size && manager.getCurrentSongIndex() != index) {
            re = manager.play(index)
        }
        return re
    }

    override fun getAudioSessionId(): Int {
        return manager.getAudioSessionId()
    }

    override fun setCurrentSong(song: Song?): Int {
        if (song == null) return -1
        return manager.prepare(song)
    }

    /**
     * 该方法并没有在 aidl 文件中声明，客户端不应该调用该方法
     *
     * 播放列表对应下标
     */
    fun play(index: Int): Int {
        return manager.play(index)
    }

    @Synchronized
    override fun pre(): Song {
        val pre = manager.getCurrentSong()
        val s = manager.preSong()
        return s!!
    }

    @Synchronized
    override fun next(): Song {
        val pre = manager.getCurrentSong()
        // 随机播放时可能播放同一首歌曲
        val next = manager.nextSong()
        return next!!
    }

    @Synchronized
    override fun pause(): Int {
        return manager.pause()
    }

    @Synchronized
    override fun resume(): Int {
        return manager.resume()
    }

    override fun currentSong(): Song {
        return manager.getCurrentSong()!!
    }

    override fun currentSongIndex(): Int {
        return manager.getCurrentSongIndex()
    }

    override fun status(): Int {
        return manager.getPlayState()
    }

    override fun setPlayList(songs: MutableList<Song>?, current: Int, id: Int): Song? {
        if (songs!!.size <= 0) {
            return null
        }

        var cu = 0
        if (current >= 0 && current < songs.size) {
            cu = current
        }
        return manager.setPlayList(songs as ArrayList<Song>, cu, id)
    }

    override fun setPlaySheet(sheetID: Int, current: Int): Song? {
        return manager.setPlaySheet(sheetID, current)
    }

    override fun getPlayList(): MutableList<Song> {
        return manager.getSongList() as MutableList<Song>
    }

    @Synchronized
    override fun setPlayMode(mode: Int) {
        if (mode >= PlayController.MODE_DEFAULT && mode <= PlayController.MODE_RANDOM) manager.setPlayMode(mode)
    }

    override fun getProgress(): Int {
        return manager.getProgress()
    }

    override fun seekTo(pos: Int): Int {
        return manager.seekTo(pos)
    }

    override fun remove(song: Song?) {
        manager.remove(song)
    }

    override fun getPlayMode(): Int {
        return manager.getPlayMode()
    }

    override fun getPlayListId(): Int {
        return manager.getPlayListId()
    }

    override fun registerOnSongChangedListener(li: IOnSongChangedListener?) {
        mSongChangeListeners.register(li)
    }

    override fun registerOnPlayStatusChangedListener(li: IOnPlayStatusChangedListener?) {
        mStatusChangeListeners.register(li)
    }

    override fun registerOnPlayListChangedListener(li: IOnPlayListChangedListener?) {
        mPlayListChangeListeners.register(li)
    }

    override fun registerOnDataIsReadyListener(li: IOnDataIsReadyListener?) {
        mDataIsReadyListeners.register(li)
    }

    override fun unregisterOnSongChangedListener(li: IOnSongChangedListener?) {
        mSongChangeListeners.unregister(li)
    }

    override fun unregisterOnPlayStatusChangedListener(li: IOnPlayStatusChangedListener?) {
        mStatusChangeListeners.unregister(li)
    }

    override fun unregisterOnPlayListChangedListener(li: IOnPlayListChangedListener?) {
        mPlayListChangeListeners.unregister(li)
    }

    override fun unregisterOnDataIsReadyListener(li: IOnDataIsReadyListener?) {
        mDataIsReadyListeners.unregister(li)
    }

    fun releashMediaPlayer() {
        manager.releaseMediaPlayer()

        if (focusManager != null) {
            // 释放音乐焦点
            focusManager.abandonAudioFocus()
        }
    }

    fun notifyDataIsReady() {
        val N = mDataIsReadyListeners.beginBroadcast()
        for (i in 0 until N) {
            val listener = mDataIsReadyListeners.getBroadcastItem(i)
            if (listener != null) {
                try {
                    listener.dataIsReady()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mDataIsReadyListeners.finishBroadcast()
    }

    private inner class NotifyStatusChange : PlayController.NotifyStatusChanged {
        override fun notify(song: Song?, index: Int, status: Int) {
            val N = mStatusChangeListeners.beginBroadcast()
            for (i in 0 until N) {
                val listener = mStatusChangeListeners.getBroadcastItem(i)
                if (listener != null) {
                    try {
                        when (status) {
                            PlayController.STATUS_START -> listener.playStart(song, index, status)
                            PlayController.STATUS_STOP -> listener.playStop(song, index, status)
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private inner class NotifySongChange : PlayController.NotifySongChanged {
        override fun notify(song: Song?, index: Int, isNext: Boolean) {
            val N = mSongChangeListeners.beginBroadcast()
            for (i in 0 until N) {
                val listener = mSongChangeListeners.getBroadcastItem(i)
                if (listener != null) {
                    try {
                        listener.onSongChange(song, index, isNext)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
            mSongChangeListeners.finishBroadcast()
        }
    }

    private inner class NotifyPlayListChange : PlayController.NotifyPlayListChanged {
        override fun notify(current: Song?, index: Int, id: Int) {
            val N = mPlayListChangeListeners.beginBroadcast()
            for (i in 0 until N) {
                val listener = mPlayListChangeListeners.getBroadcastItem(i)
                if (listener != null) {
                    try {
                        listener.onPlayListChange(current, index, id)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}




























