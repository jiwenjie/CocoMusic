package com.jiwenjie.cocomusic.play.playservice

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.PowerManager
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService.Companion.RELEASE_WAKELOCK
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService.Companion.TRACK_PLAY_ENDED
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService.Companion.TRACK_WENT_TO_NEXT
import java.lang.ref.WeakReference

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/02
 *  desc:
 *  version:1.0
 */
class MusicPlayerEngine(service: MusicPlayerService) :
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnPreparedListener {

    private val TAG = MusicPlayerEngine::class.java.simpleName

    private var mService: WeakReference<MusicPlayerService>? = null
    private var mCurrentMediaPlayer: MediaPlayer? = null

    private var mHandler: Handler? = null

    // 是否已经初始化
    private var mIsInitialized = false
    // 是否已经初始化
    private var mIsPrepared = false

    init {
        mService = WeakReference(service)
        mCurrentMediaPlayer = MediaPlayer()
        mCurrentMediaPlayer!!.setWakeMode(mService!!.get(), PowerManager.PARTIAL_WAKE_LOCK)
    }

    fun setDataSource(path: String) {
        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer!!, path)
    }

    private fun setDataSourceImpl(player: MediaPlayer, path: String?): Boolean {
        if (path == null) return false
        try {
            if (player.isPlaying) player.stop()
            mIsPrepared = false
            player.reset()
            if (path.startsWith("content://")) {
                player.setDataSource(mService!!.get()!!, Uri.parse(path))     // todo
            } else {
                player.setDataSource(path)
            }
            player.prepareAsync()
            player.setOnPreparedListener(this)
            player.setOnBufferingUpdateListener(this)
            player.setOnErrorListener(this)
            player.setOnCompletionListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun setHandler(handler: Handler) {
        this.mHandler = handler
    }

    fun isInitialized(): Boolean {
        return mIsInitialized
    }

    fun isPrepared(): Boolean {
        return mIsPrepared
    }

    fun start() {
        mCurrentMediaPlayer!!.start()
    }

    fun stop() {
        try {
            mCurrentMediaPlayer!!.reset()
            mIsInitialized = false
            mIsPrepared = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        mCurrentMediaPlayer!!.release()
    }

    fun pause() {
        mCurrentMediaPlayer!!.pause()
    }

    fun isPlaying(): Boolean {
        return mCurrentMediaPlayer!!.isPlaying
    }

    /**
     * getDuration 只能在 prepared 之后才能调用，否则会报 -38 错误
     */
    fun getDuration(): Long {
        if (mIsPrepared) return mCurrentMediaPlayer!!.duration.toLong()
        return 0
    }

    fun position(): Long {
        return try {
            mCurrentMediaPlayer!!.currentPosition.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun seek(whereto: Long) {
        mCurrentMediaPlayer!!.seekTo(whereto.toInt())
    }

    fun getCurrentPosition(): Long {
        return try {
            mCurrentMediaPlayer!!.currentPosition.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun seekTo(whereto: Long) {
        mCurrentMediaPlayer!!.seekTo(whereto.toInt())
    }

    fun setVolume(vol: Float) {
        LogUtils.e("Volume vol = $vol")
        try {
            mCurrentMediaPlayer!!.setVolume(vol, vol)   // todo what does this function do ?( 这个函数有什么作用 )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAudioSessionId(): Int {
        return mCurrentMediaPlayer!!.audioSessionId
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        LogUtils.e("Music Server Error what：$what extra: $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_SERVER_DIED, MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                val service = mService!!.get()
                val errorInfo = TrackErrorInfo(service!!.getAudioId(), service.getTitle())
                mIsInitialized = false
                mCurrentMediaPlayer!!.release()
                mCurrentMediaPlayer = MediaPlayer()
                mCurrentMediaPlayer!!.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK)
                val msg = mHandler!!.obtainMessage(MusicPlayerService.TRACK_PLAY_ERROR, errorInfo)
                mHandler!!.sendMessageDelayed(msg, 2000)
                return true
            }
            else -> {
            }
        }
        return true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        LogUtils.e("$TAG + onCompletion")
        if (mp === mCurrentMediaPlayer) {
            mHandler?.sendEmptyMessage(TRACK_WENT_TO_NEXT)
        } else {
            mService?.get()?.mWakeLock?.acquire(30000)
            mHandler?.sendEmptyMessage(TRACK_PLAY_ENDED)
            mHandler?.sendEmptyMessage(RELEASE_WAKELOCK)
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        LogUtils.e("$TAG + onBufferingUpdate + $percent")
        val message = mHandler!!.obtainMessage(MusicPlayerService.PREPARE_ASYNC_UPDATE, percent)
        mHandler!!.sendMessage(message)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp!!.start()
        if (!mIsPrepared) {
            mIsPrepared = true
            val msg = mHandler!!.obtainMessage(MusicPlayerService.PLAYER_PREPARED)
            mHandler!!.sendMessage(msg)
        }
    }

    private inner class TrackErrorInfo(var audioId: String?, var trackName: String?)
}






























