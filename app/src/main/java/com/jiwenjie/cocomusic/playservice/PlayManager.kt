package com.jiwenjie.cocomusic.playservice

import android.app.Activity
import android.content.*
import android.os.IBinder
import android.os.RemoteException
import com.jiwenjie.cocomusic.aidl.IMusicService
import com.jiwenjie.cocomusic.aidl.Music
import java.util.*
import kotlin.collections.ArrayList

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:
 *  version:1.0
 */
object PlayManager {

    var mService: IMusicService? = null
    private val mConnectionMap by lazy {
        WeakHashMap<Context, ServiceBinder>()
    }

    fun bindToService(context: Context,
                      callback: ServiceConnection): ServiceToken? {
        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null) {
            realActivity = context
        }
        val contextWrapper = ContextWrapper(realActivity)
        // 启动 Service
        contextWrapper.startService(
            Intent(contextWrapper, MusicPlayerService::class.java))
        // 绑定 Service
        val binder = ServiceBinder(callback,
                contextWrapper.applicationContext)
        if (contextWrapper.bindService(
                        Intent().setClass(contextWrapper, MusicPlayerService::class.java), binder, 0)) {
            mConnectionMap[contextWrapper] = binder
            return ServiceToken(contextWrapper)
        }
        return null
    }

    fun unbindFromService(token: ServiceToken?) {
        if (token == null) {
            return
        }
        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap[mContextWrapper] ?: return
        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap.isEmpty()) {
            mService = null
        }
    }

    fun isPlaybackServiceConnected(): Boolean {
        return mService != null
    }


    fun nextPlay(music: Music) {
        try {
            if (mService != null)
                mService!!.nextPlay(music)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun playOnline(music: Music) {
        try {
            if (mService != null)
                mService!!.playMusic(music)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun play(id: Int) {
        try {
            if (mService != null)
                mService!!.play(id)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun play(id: Int, musicList: List<Music>, pid: String) {
        try {
            if (mService != null) {
                mService!!.playPlaylist(musicList, id, pid)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun getAudioSessionId(): Int {
        try {
            if (mService != null)
                return mService!!.AudioSessionId()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return 0
    }

    fun playPause() {
        try {
            if (mService != null)
                mService!!.playPause()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun prev() {
        try {
            if (mService != null)
                mService!!.prev()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    operator fun next() {
        try {
            if (mService != null)
                mService!!.next()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun setLoopMode(loopmode: Int) {
        try {
            if (mService != null)
                mService!!.setLoopMode(loopmode)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun seekTo(ms: Int) {
        try {
            if (mService != null)
                mService!!.seekTo(ms)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    fun position(): Int {
        try {
            if (mService != null)
                return mService!!.position()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return 0
    }

    fun getCurrentPosition(): Int {
        try {
            if (mService != null)
                return mService!!.currentPosition
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return 0
    }

    fun getDuration(): Int {
        try {
            if (mService != null)
                return mService!!.duration
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return 0
    }

    fun getSongName(): String {
        try {
            if (mService != null)
                return mService!!.songName
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return "湖科音乐"
    }

    fun getSongArtist(): String {
        try {
            if (mService != null)
                return mService!!.songArtist
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return "湖科音乐"
    }

    fun isPlaying(): Boolean {
        try {
            if (mService != null)
                return mService!!.isPlaying
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return false
    }

    fun isPause(): Boolean {
        try {
            if (mService != null)
                return mService!!.isPause
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return false
    }

    fun getPlayingMusic(): Music? {
        try {
            if (mService != null)
                return mService!!.playingMusic
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return null
    }

    fun getPlayingId(): String {
        try {
            if (mService != null && mService!!.playingMusic != null)
                return mService!!.playingMusic.mid!!
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return "-1"
    }

    fun getPlayList(): List<Music> {
        try {
            if (mService != null)
                return mService!!.playList
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        return java.util.ArrayList()
    }

    fun setPlayList(playlist: List<Music>) {

    }

    fun clearQueue() {
        try {
            if (mService != null) {
                mService!!.clearQueue()
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun removeFromQueue(adapterPosition: Int) {
        try {
            if (mService != null)
                mService!!.removeFromQueue(adapterPosition)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun showDesktopLyric(isShow: Boolean) {
        try {
            if (mService != null)
                mService!!.showDesktopLyric(isShow)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    class ServiceBinder(private val mCallback: ServiceConnection?,
                        private val mContext: Context)
        : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mService = IMusicService.Stub.asInterface(service)
            mCallback?.onServiceConnected(className, service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            mCallback?.onServiceDisconnected(className)
            mService = null
        }
    }

    class ServiceToken(var mWrappedContext: ContextWrapper)
}

















