@file:Suppress("DEPRECATION")

package com.jiwenjie.cocomusic.one.service

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.RemoteException
import com.jiwenjie.cocomusic.aidl.IPlayControl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:服务端调用
 *  参考文章 http://www.jianshu.com/p/bc2f779a5400，波尼音乐的作者
 *  version:1.0
 */
class AudioFocusManagerFirst(context: Context, control: IPlayControl) : AudioManager.OnAudioFocusChangeListener {

   private var mAudioManager: AudioManager? = null

   private var isPausedByFocusLossTransient = false
   private var mVolumeWhenFocusLossTransientCanDuck = -1

   private var mContext: Context? = null
   private var IControl: IPlayControl? = null

   init {
      this.mContext = context
      this.IControl = control
      mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        AudioFocusRequest.Builder()
   }

   /**
    * 播放音乐前先请求音频焦点
    */
   fun requestAudioFocus(): Boolean {
      /** 这里的方法已过时，在 Android O 以上必须使用最新的方法，所以为了兼容性这里需要更新调整，暂时不做处理**/
      return mAudioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) ==
              AudioManager.AUDIOFOCUS_REQUEST_GRANTED
   }

   /**
    * 退出播放器后不再占用音频焦点
    */
   fun abandonAudioFocus() {
      mAudioManager!!.abandonAudioFocus(this)
   }

   /**
    * 音频焦点监听回调
    */
   override fun onAudioFocusChange(focusChange: Int) {
      var volume: Int
      when (focusChange) {
         AudioManager.AUDIOFOCUS_GAIN -> {       // 重新获得焦点
            if (!willPlay() && isPausedByFocusLossTransient) {
               // 通话结束
               play()
            }
            volume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (mVolumeWhenFocusLossTransientCanDuck > 0 && volume == mVolumeWhenFocusLossTransientCanDuck / 2) {
               // 恢复音量
               mAudioManager!!.setStreamVolume(
                       AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck,
                       AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
               )
            }

            isPausedByFocusLossTransient = false
            mVolumeWhenFocusLossTransientCanDuck = 0
         }
         AudioManager.AUDIOFOCUS_LOSS -> {      // 永久丢失焦点，如被其他播放器抢占
            if (willPlay()) {
               forceStop()
            }
         }
         AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {     // 短暂丢失焦点，如通知
            // 音量减小为之前的一半
            volume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (willPlay() && volume > 0) {
               mVolumeWhenFocusLossTransientCanDuck = volume
               mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, mVolumeWhenFocusLossTransientCanDuck / 2,
                       AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE)
            }
         }
      }
   }

   private fun play() {
      try {
         IControl!!.resume()
      } catch (e: RemoteException) {
         e.printStackTrace()
      }
   }

   private fun forceStop() {
      try {
         IControl!!.pause()
      } catch (e: RemoteException) {
         e.printStackTrace()
      }
   }

   private fun willPlay(): Boolean {
      return IControl!!.status() == PlayController.STATUS_PLAYING
   }
}
































