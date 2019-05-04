package com.jiwenjie.cocomusic.playservice

import android.app.Activity
import android.content.*
import android.os.IBinder
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
    private var mConnectionMap: WeakHashMap<Context, ServiceBinder>? = WeakHashMap()

    class ServiceToken(var mWrappedContext: ContextWrapper)

    class ServiceBinder(private val mCallback: ServiceConnection?, private val mContext: Context)
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

    @JvmStatic
    fun bindToService(context: Context, callback: ServiceConnection): ServiceToken? {
        var realActivity: Activity? = (context as Activity).parent
        if (realActivity == null) {
            realActivity = context
        }

        val contextWrapper = ContextWrapper(realActivity)
        contextWrapper.startService(Intent(contextWrapper, MusicPlayerService::class.java))
        val binder =
            ServiceBinder(callback, contextWrapper.applicationContext)
        if (contextWrapper.bindService(Intent().setClass(contextWrapper, MusicPlayerService::class.java), binder, 0)) {
            mConnectionMap!!.put(contextWrapper, binder)
            return ServiceToken(contextWrapper)
        }
        return null
    }

    @JvmStatic
    fun unbindFromService(token: ServiceToken?) {
        if (token == null) return

        val mContextWrapper = token.mWrappedContext
        val mBinder = mConnectionMap!![mContextWrapper] ?: return

        mContextWrapper.unbindService(mBinder)
        if (mConnectionMap!!.isEmpty()) {
            mService = null
        }
    }

    @JvmStatic
    fun isPlaybackServiceConnected(): Boolean {
        return mService != null
    }

    @JvmStatic
    fun nextPlay(music: Music) {
        try {
            if (mService != null) {
                mService!!.nextPlay(music)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun playOnline(music: Music) {
        try {
            if (mService != null) {
                mService!!.playMusic(music)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun play(id: Int) {
        try {
            if (mService != null) {
                mService!!.play(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun play(musicList: ArrayList<Music>, id: Int, pid: String) {
        try {
            if (mService != null) {
                mService!!.playPlaylist(musicList, id, pid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getAudioSessionId() : Int {
        try {
            if (mService != null) return mService!!.AudioSessionId()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun playPause() {
        try {
            if (mService != null) mService!!.playPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun prev() {
        try {
            if (mService != null) mService!!.prev()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun next() {
        try {
            if (mService != null) mService!!.next()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setLoopMode(loopmode: Int) {
        try {
            if (mService != null) mService!!.setLoopMode(loopmode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun seekTo(ms: Int) {
        try {
            if (mService != null) mService!!.seekTo(ms)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun position(): Int {
        try {
            if (mService != null) return mService!!.position()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun getCurrentPosition(): Int {
        try {
            if (mService != null) return mService!!.currentPosition
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    @JvmStatic
    fun getDuration(): Int {
        try {
            if (mService != null) return mService!!.duration
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    @JvmStatic
    fun getSongName(): String {
        try {
            if (mService != null) return mService!!.songName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "CocoMusic"
    }

    @JvmStatic
    fun getSongArtist(): String {
        try {
            if (mService != null) return mService!!.songArtist
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "CocoMusicName"
    }

    @JvmStatic
    fun isPlaying(): Boolean {
        try {
            if (mService != null) return mService!!.isPlaying
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun isPause(): Boolean {
        try {
            if (mService != null) return mService!!.isPause
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic
    fun getPlayingMusic(): Music? {
        try {
            if (mService != null) return mService!!.playingMusic
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun getPlayingId(): String {
        try {
            if (mService != null && mService!!.playingMusic != null) return mService!!.playingMusic.mid!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "-1"
    }

    @JvmStatic
    fun getPlayList(): ArrayList<Music> {
        try {
            if (mService != null) return mService!!.playList as ArrayList<Music>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    @JvmStatic
    fun setPlayList(playList: ArrayList<Music>) {

    }

    @JvmStatic
    fun clearQueue() {
        try {
            if (mService != null) {
                mService!!.clearQueue()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun removeFromQueue(adapterPosition: Int) {
        try {
            if (mService != null) mService!!.removeFromQueue(adapterPosition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun showDesktopLyric(isShow: Boolean) {
        try {
            if (mService != null) {
                mService!!.showDesktopLyric(isShow)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

















