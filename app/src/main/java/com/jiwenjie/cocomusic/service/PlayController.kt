package com.jiwenjie.cocomusic.service

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import com.jiwenjie.cocomusic.aidl.Song
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/24
 *  desc:
 *  version:1.0
 */
class PlayController private constructor(
    context: Context, focusManager: AudioFocusManager,
    sessionManager: MediaSessionManager,
    sl: NotifyStatusChanged,
    sc: NotifySongChanged,
    pl: NotifyPlayListChanged) {

    private var context: Context? = null

    private var focusManager: AudioFocusManager? = null
    private var sessionManager: MediaSessionManager? = null

    private var mCurrentSong = 0
    private var mPlayState = -99

    private var mPlayList: MutableList<Song> = Collections.synchronizedList(ArrayList())
    private var mPlayer: MediaPlayer? = null

    private var isNext = true
    private var mPlayListId = -99

    // MediaPlayer 是否调用过 setDataSource
    // 否则第一次调用 changeSong 里的 _.reset 方法时 MediaPlayer 会抛出 IllegalStateException
    private var hasMediaPlayerInit = false

    private var mNotifyStatusChanged: NotifyStatusChanged? = null
    private var mNotifySongChanged: NotifySongChanged? = null
    private var mNotifyPlayListChanged: NotifyPlayListChanged? = null

    init {
        this.context = context
        this.focusManager = focusManager
        this.sessionManager = sessionManager
        this.mNotifyStatusChanged = sl
        this.mNotifySongChanged = sc
        this.mNotifyPlayListChanged = pl

        this.mPlayState = STATUS_STOP
        this.mPlayer = MediaPlayer()
        this.mPlayer!!.setOnCompletionListener {
            nextSong()
        }
    }

    companion object {
        //未知错误
        const val ERROR_UNKNOWN = -1

        const val ERROR_INVALID = -2

        //歌曲文件解码错误
        const val ERROR_DECODE = -3

        //没有指定歌曲
        const val ERROR_NO_RESOURCE = -4

        //正在播放
        const val STATUS_PLAYING = 10

        //播放结束
        const val STATUS_COMPLETE = 11

        //开始播放
        const val STATUS_START = 12

        //播放暂停
        const val STATUS_PAUSE = 13

        //停止
        const val STATUS_STOP = 14

        //默认播放模式，列表播放，播放至列表末端时停止播放
        const val MODE_DEFAULT = 20

        //列表循环
        const val MODE_LIST_LOOP = 21

        //单曲循环
        const val MODE_SINGLE_LOOP = 22

        //随机播放
        const val MODE_RANDOM = 23

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var MANAGER: PlayController? = null

        fun getMediaContController(context: Context, focusManager: AudioFocusManager,
                                   sessionManager: MediaSessionManager,
                                   sl: NotifyStatusChanged,
                                   sc: NotifySongChanged,
                                   pl: NotifyPlayListChanged): PlayController {
            if (MANAGER == null) {
                synchronized(PlayController::class.java) {
                    if (MANAGER == null) {
                        MANAGER = PlayController(context, focusManager, sessionManager, sl, sc, pl)
                    }
                }
            }
            return MANAGER!!
        }
    }

    // 设置播放模式 -> 初始化为默认
    private var mPlayMode = MODE_DEFAULT

    interface NotifyStatusChanged {
        fun notify(song: Song?, index: Int, status: Int)
    }

    interface NotifySongChanged {
        fun notify(song: Song?, index: Int, isNext: Boolean)
    }

    interface NotifyPlayListChanged {
        fun notify(current: Song?, index: Int, id: Int)
    }


    /************************************  功能方法 ****************************************/

    // 设置播放模式
    fun setPlayMode(mode: Int) {
        this.mPlayMode = mode
    }

    // 获得播放模式
    fun getPlayMode(): Int {
        return this.mPlayMode
    }

    // 返回当前的播放列表
    fun getSongList(): List<Song> {
        return mPlayList
    }

    // 设置当前播放列表
    fun setPlayList(songs: ArrayList<Song>, current: Int, id: Int): Song {
        this.mPlayList = songs
        this.mPlayListId = id

        mCurrentSong = current
        changeSong()

        val currentS = songs[mCurrentSong]
        mNotifyPlayListChanged!!.notify(currentS, current, id)

        return currentS
    }

    fun getPlayListId(): Int {
        return mPlayListId
    }

    /**
     * 设置播放片  sheet(片) todo 暂时不写该方法
      */
    fun setPlaySheet(sheetID: Int, current: Int): Song? {
        return null
    }

    /**
     * 当前正在播放的曲目
     */
    fun getCurrentSong(): Song? {
        return if (mPlayList.size == 0) null else mPlayList[mCurrentSong]
    }

    fun getCurrentSongIndex(): Int {
        return mCurrentSong
    }

    /**
     * 播放指定曲目
     */
    fun play(song: Song?): Int {
        return play(mPlayList.indexOf(song))
    }

    fun play(index: Int): Int {
        var result = ERROR_INVALID
        if (index != -1) {  // 列表中有该歌曲
            if (mCurrentSong != index) {    // 判断不是当前歌曲
                isNext = mCurrentSong < index
                mCurrentSong = index
                if (mPlayState != STATUS_PLAYING) {
                    mNotifyStatusChanged!!.notify(getCurrentSong(), mCurrentSong, STATUS_START)
                    mPlayState = STATUS_PLAYING     // 切换并播放
                }
                result = changeSong()
            } else if (mPlayState != STATUS_PLAYING) {
                // 是当前歌曲但没在播放
                mPlayState = STATUS_PAUSE
                resume()    // 开始播放歌曲
            } else {
                // 是当前歌曲且已经在播放
                return 1
            }
        } else {
            return ERROR_NO_RESOURCE
        }
        return result
    }

    fun prepare(song: Song) : Int {
        var result = ERROR_INVALID
        val index = mPlayList.indexOf(song)
        if (index != -1) {
            // 列表中有该歌曲的话
            if (mCurrentSong != index) {
                // 不是当前歌曲的话
                mCurrentSong = index
                if (mPlayState == STATUS_PLAYING) {
                    pause()
                }
                result = changeSong()
            }
        } else {
            return ERROR_NO_RESOURCE
        }
        return result
    }

    /**
     * 获得播放状态
     */
    fun getPlayState(): Int {
        return mPlayState
    }

    /**
     * 释放播放器，服务端停止时，改方法才会被调用
     */
    fun releaseMediaPlayer() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayState = STATUS_STOP

            sessionManager!!.release()
        }
    }

    /**
     * 切换曲目
     */
    @Synchronized
    private fun changeSong(): Int {
        if (mPlayState == STATUS_PLAYING || mPlayState == STATUS_PAUSE) {
            mPlayer!!.stop()
        }

        if (hasMediaPlayerInit) {
            mPlayer!!.reset()
        }

        if (mPlayList.size == 0) {
            mCurrentSong = 0
            mNotifySongChanged!!.notify(null, -1, isNext)
            return ERROR_NO_RESOURCE
        } else {
            val next = mPlayList[mCurrentSong].path
            try {
                sessionManager!!.updateMetaData(next!!)
                mPlayer!!.setDataSource(next)
                if (!hasMediaPlayerInit) {
                    hasMediaPlayerInit = true
                }
                mPlayer!!.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
                return ERROR_DECODE
            }

            if (mPlayState == STATUS_PLAYING) {
                focusManager!!.requestAudioFocus()
                sessionManager!!.updatePlaybackState()
                mPlayer!!.start()
            }

            mNotifySongChanged!!.notify(getCurrentSong(), mCurrentSong, isNext)
            return 1
        }
    }

    /**
     * 上一曲
     */
    fun preSong(): Song? {
        isNext = false
        when(mPlayMode) {
            MODE_SINGLE_LOOP -> {   // 单曲循环
                changeSong()
            }
            MODE_RANDOM -> {    // 随机播放
                val pre = Random().nextInt(mPlayList.size)
                if (pre != mCurrentSong) {
                    mCurrentSong = pre
                    changeSong()
                }
            }
            else -> {
                if (mCurrentSong == 0) {
                    mCurrentSong = mPlayList.size - 1
                } else {
                    mCurrentSong --
                }
                changeSong()
            }
        }
        return if (mPlayList.size == 0) {
            null
        } else {
            mPlayList[mCurrentSong]
        }
    }

    // 播放下一曲音乐
    fun nextSong(): Song? {
        isNext = true
        when (mPlayMode) {
            MODE_SINGLE_LOOP -> {   // 单曲循环
                changeSong()
            }
            MODE_LIST_LOOP -> {     // 列表循环
                if (mCurrentSong == mPlayList.size - 1) {
                    mCurrentSong = 0
                } else {
                    mCurrentSong++
                }
                changeSong()
            }
            MODE_RANDOM -> {    //随机播放
                // UPDATE 修复正在播放的歌单最后一首歌曲被移除歌单时 mPlayList.size == 0 使得 nextInt 方法出错
                val next = Random().nextInt(mPlayList.size)
                if (next != mCurrentSong) {
                    mCurrentSong = next
                    changeSong()
                }
            }
            else -> {
                if (mCurrentSong == mPlayList.size - 1) {
                    // 最后一首
                    mCurrentSong = 0
                    changeSong()
                    pause()     // 暂停播放
                } else {
                    mCurrentSong++
                    changeSong()
                }
            }
        }
        return if (mPlayList.size == 0) {
            null
        } else {
            mPlayList[mCurrentSong]
        }
    }

    /**
     * 暂停播放当前歌曲
     */
    fun pause(): Int {
        if (mPlayState == STATUS_PLAYING) {
            sessionManager!!.updatePlaybackState()
            mPlayer!!.pause()
            mPlayState = STATUS_PAUSE

            // 放在最后 在 mPlayState 修改之后
            mNotifyStatusChanged!!.notify(getCurrentSong(), mCurrentSong, STATUS_STOP)
        }
        return mPlayState
    }

    /**
     * 继续播放当前歌曲
     */
    fun resume(): Int {
        if (mPlayState != STATUS_PLAYING) {
            focusManager!!.requestAudioFocus()
            sessionManager!!.updatePlaybackState()
            mPlayer!!.start()
            mPlayState = STATUS_PLAYING

            mNotifyStatusChanged!!.notify(getCurrentSong(), mCurrentSong, STATUS_START)
        }
        return 1
    }

    /**
     * 拖动到指定位置播放
     */
    fun seekTo(to: Int): Int {
        sessionManager!!.updatePlaybackState()
        mPlayer!!.seekTo(to)
        return 1
    }

    /**
     * 获得播放进度
     */
    fun getProgress(): Int {
        return mPlayer!!.currentPosition
    }

    /**
     * 用于提取频谱
     */
    fun getAudioSessionId(): Int {
        return mPlayer!!.audioSessionId
    }

    fun remove(song: Song?) {
        if (song == null) return

        var index = mPlayList.indexOf(song)
        if (index != -1) {
            if (mCurrentSong == index) {
                var tempS = mPlayMode
                mPlayMode = MODE_LIST_LOOP
                mPlayList.removeAt(index)
                mCurrentSong--
                nextSong()
                mPlayMode = tempS
            } else {
                mPlayList.removeAt(index)
                if (index < mCurrentSong) {
                    mCurrentSong--
                }
            }

            if (mPlayList.size == 0 || mCurrentSong < 0) {
                // 服务器的播放列表是空的，可能是因为仅有一首歌曲的播放列表被清空
                // 此时重新设置为【全部歌曲】该过程在服务端完成，若在客户端的 onPlayListChange
                // 回调中重置播放列表会出现异常，beginBroadcast() called while already in a broadcast
                setPlaySheet(2, 0) // todo 该方法记得重写
            } else {
                mNotifyPlayListChanged!!.notify(mPlayList[mCurrentSong], mCurrentSong, mPlayListId)
            }
        }
    }
}
















