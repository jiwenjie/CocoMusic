package com.jiwenjie.cocomusic.aidl

import android.content.Context
import android.os.RemoteCallbackList

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
class PlayControlImpl(context: Context): IPlayControl.Stub() {

    init {
        val mSongChangeListeners: RemoteCallbackList<IOnSongChangedListener> = RemoteCallbackList()
        val mStatusChangeListeners: RemoteCallbackList<IOnPlayStatusChangedListener> = RemoteCallbackList()
        val mPlayListChangeListeners: RemoteCallbackList<IOnPlayListChangedListener> = RemoteCallbackList()
        val mDataIsReadyListeners: RemoteCallbackList<IOnDataIsReadyListener> = RemoteCallbackList()

        
    }

    override fun play(whitch: Song?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playByIndex(index: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrentSong(song: Song?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pre(): Song {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun next(): Song {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resume(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentSong(): Song {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentSongIndex(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun status(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlayList(songs: MutableList<Song>?, current: Int, id: Int): Song {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlaySheet(sheetID: Int, current: Int): Song {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlayList(): MutableList<Song> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlayListId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSongChangedListener(li: IOnSongChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnPlayStatusChangedListener(li: IOnPlayStatusChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnPlayListChangedListener(li: IOnPlayListChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnDataIsReadyListener(li: IOnDataIsReadyListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnSongChangedListener(li: IOnSongChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnPlayStatusChangedListener(li: IOnPlayStatusChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnPlayListChangedListener(li: IOnPlayListChangedListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnDataIsReadyListener(li: IOnDataIsReadyListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlayMode(mode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProgress(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(song: Song?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPlayMode(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}




























