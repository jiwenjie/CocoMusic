@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.jiwenjie.cocomusic.play

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService.Companion.AUDIO_FOCUS_CHANGE
import com.jiwenjie.cocomusic.utils.SystemUtils

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc 音频管理类
 * 主要用来管理音频焦点
 *  version:1.0
 */
@Suppress("DEPRECATION")
class AudioAndFocusManager(context: Context, mHandler: MusicPlayerService.MusicPlayerHandler) {

    private var mAudioManager: AudioManager? = null
    private var mediaButtonReceiverComponent: ComponentName? = null
    private var mPendingIntent: PendingIntent? = null
    private var mediaSession: MediaSession? = null
    private var mHandler: MusicPlayerService.MusicPlayerHandler? = null

    init {
        this.mHandler = mHandler
        initAudioManager(context)
    }

    /**
     * 初始化AudioManager&Receiver
     *
     * @param mContext
     */
    private fun initAudioManager(mContext: Context) {
        mediaSession = MediaSession(mContext, "AudioAndFocusManager")
        mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaButtonReceiverComponent = ComponentName(
            mContext.packageName,
            MediaButtonIntentReceiver::class.java.name
        )
        mContext.packageManager.setComponentEnabledSetting(
            mediaButtonReceiverComponent!!,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
        mAudioManager!!.registerMediaButtonEventReceiver(mediaButtonReceiverComponent)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.component = mediaButtonReceiverComponent
        mPendingIntent = PendingIntent.getBroadcast(
            mContext, 0,
            mediaButtonIntent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        mediaSession!!.setMediaButtonReceiver(mPendingIntent)
    }

    /**
     * 请求音频焦点
     */
    fun requestAudioFocus() {
        if (SystemUtils.isO()) {
            val mAudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            val res = mAudioManager!!.requestAudioFocus(mAudioFocusRequest)
            if (res == 1) {
                LogUtils.e("requestAudioFocus=" + true)
            }
        } else {
            val result = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager!!.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
            LogUtils.e("requestAudioFocus=$result")
        }
    }

    /**
     * 关闭音频焦点
     */
    fun abandonAudioFocus() {
        val result = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager!!.abandonAudioFocus(audioFocusChangeListener)
        LogUtils.e("requestAudioFocus=$result")
    }


    /**
     * 音频焦点改变监听器
     */
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        LogUtils.e("OnAudioFocusChangeListener", focusChange.toString() + "---")
        mHandler.obtainMessage(AUDIO_FOCUS_CHANGE, focusChange, 0).sendToTarget()
    }
}

