package com.jiwenjie.cocomusic.play

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.RemoteException
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.jiwenjie.cocomusic.aidl.Music

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:
 *  version:1.0
 */
class MediaSessionManager(context: Context, control: IMusicServiceStub, mHandler: Handler) {

    private val TAG = MediaSessionManager::class.java.simpleName

    //指定可以接收的来自锁屏页面的按键信息
    private val MEDIA_SESSION_ACTIONS = (
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_SEEK_TO)


    private var control: IMusicServiceStub? = null
    private var context: Context? = null
    private var mMediaSession: MediaSessionCompat? = null
    private var mHandler: Handler? = null

    init {
        this.context = context
        this.control = control
        this.mHandler = mHandler
        setupMediaSession()
    }

    /**
     * 初始化并激活 MediaSession
     */
    private fun setupMediaSession() {
        // 第二个参数 tag: 这个是用于调试用的,随便填写即可
        mMediaSession = MediaSessionCompat(context, TAG)
        // 指明支持的按键信息类型
        mMediaSession!!.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mMediaSession!!.setCallback(callback, mHandler)
        mMediaSession!!.isActive = true
    }

    /**
     * 更新播放状态，播放 / 暂停 / 拖动进度条时调用
     */
    fun updatePlaybackState() {
        val state = if (isPlaying())
            PlaybackStateCompat.STATE_PLAYING
        else
            PlaybackStateCompat.STATE_PAUSED

        mMediaSession!!.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state, getCurrentPosition(), 1f)
                .build())
    }

    private fun getCurrentPosition(): Long {
        return try {
            control!!.currentPosition.toLong()
        } catch (e: RemoteException) {
            e.printStackTrace()
            0
        }
    }

    /**
     * 是否在播放
     * @return
     */
    protected fun isPlaying(): Boolean {
        return try {
            control!!.isPlaying
        } catch (e: RemoteException) {
            e.printStackTrace()
            false
        }
    }

    private fun getCount(): Long? {
        return try {
            control!!.playList?.size?.toLong()
        } catch (e: RemoteException) {
            e.printStackTrace()
            0
        }

    }

    /**
     * 更新正在播放的音乐信息，切换歌曲时调用
     */
    fun updateMetaData(songInfo: Music?) {
        if (songInfo == null) {
            mMediaSession!!.setMetadata(null)
            return
        }

        val metaDta = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songInfo.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songInfo.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songInfo.album)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, songInfo.artist)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songInfo.duration)

//        CoverLoader.loadBigImageView(context, songInfo, { bitmap ->
//            metaDta.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
//            mMediaSession.setMetadata(metaDta.build())
//        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaDta.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getCount()!!)
        }
        mMediaSession!!.setMetadata(metaDta.build())
    }

    fun getMediaSession(): MediaSessionCompat.Token {
        return mMediaSession!!.sessionToken
    }

    /**
     * 释放MediaSession，退出播放器时调用
     */
    fun release() {
        mMediaSession!!.setCallback(null)
        mMediaSession!!.isActive = false
        mMediaSession!!.release()
    }

    /**
     * API 21 以上 耳机多媒体按钮监听 MediaSessionCompat.Callback
     */
    private val callback = object : MediaSessionCompat.Callback() {
        // 接收到监听事件，可以有选择的进行重写相关方法
        override fun onPlay() {
            try {
                control.playPause()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onPause() {
            try {
                control.playPause()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onSkipToNext() {
            try {
                control.next()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onSkipToPrevious() {
            try {
                control.prev()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onStop() {
            try {
                control.playPause()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onSeekTo(pos: Long) {
            try {
                control.seekTo(pos.toInt())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
}



















