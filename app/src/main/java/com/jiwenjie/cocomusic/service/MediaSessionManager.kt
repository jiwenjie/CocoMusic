package com.jiwenjie.cocomusic.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.RemoteException
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.IPlayControl
import com.jiwenjie.cocomusic.bean.SongInfo
import com.jiwenjie.cocomusic.manager.MediaManager
import com.jiwenjie.cocomusic.utils.StringUtils

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/24
 *  desc:参考文章 http://www.jianshu.com/p/bc2f779a5400
 *  version:1.0
 */
@Suppress("JAVA_CLASS_ON_COMPANION")
class MediaSessionManager constructor(context: Context, control: IPlayControl) {

   private var control: IPlayControl? = null
   private var context: Context? = null
   private var mMediaSession: MediaSessionCompat? = null
   private var mediaManager: MediaManager? = null

   init {
      this.context = context
      this.control = control
      this.mediaManager = MediaManager.getInstance()
      setupMediaSession()
   }

   companion object {
      var TAG = MediaSessionManager.javaClass.simpleName
      val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
              or PlaybackStateCompat.ACTION_PAUSE
              or PlaybackStateCompat.ACTION_PLAY_PAUSE
              or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
              or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
              or PlaybackStateCompat.ACTION_STOP
              or PlaybackStateCompat.ACTION_SEEK_TO)
   }

   /**
    * 初始化并激活 MediaSession
    */
   private fun setupMediaSession() {
      mMediaSession = MediaSessionCompat(context, TAG)
      mMediaSession!!.setFlags(
              MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                      MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
      mMediaSession!!.setCallback(callback)
      mMediaSession!!.isActive = true
   }

   private val callback = object : MediaSessionCompat.Callback() {
      override fun onPlay() {
         try {
            control.resume()
         } catch (e: RemoteException) {
            e.printStackTrace()
         }
      }

      override fun onPause() {
         try {
            control.pause()
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
            control.pre()
         } catch (e: RemoteException) {
            e.printStackTrace()
         }
      }

      override fun onStop() {
         try {
            control.pause()
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

   /**
    * 更新播放状态，播放/暂停/拖动进度条时调用
    */
   fun updatePlaybackState() {
      val state = if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
      mMediaSession!!.setPlaybackState(PlaybackStateCompat.Builder()
              .setActions(MEDIA_SESSION_ACTIONS)
              .setState(state, getCurrentPosition(), 1f)
              .build())
   }

   private fun isPlaying(): Boolean {
      return try {
         control!!.status() == PlayController.STATUS_PLAYING
      } catch (e: Exception) {
         e.printStackTrace()
         false
      }
   }

   private fun getCurrentPosition(): Long {
      return try {
         control!!.progress.toLong()
      } catch (e: Exception) {
         e.printStackTrace()
         0
      }
   }

   /**
    * 更新正在播放的音乐信息，切换歌曲时调用
    */
   fun updateMetaData(path: String) {
      if (!StringUtils.isReal(path)) {
         mMediaSession!!.setMetadata(null)
         return
      }

      val info = mediaManager!!.getSongInfo(context!!, path)
      val metaData = MediaMetadataCompat.Builder()
              .putString(MediaMetadataCompat.METADATA_KEY_TITLE, info!!.title)
              .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.artist)
              .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, info.album)
              .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, info.artist)
              .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.duration)
              .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, getCoverBitmap(info))

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         metaData.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, getCount())
      }
      mMediaSession!!.setMetadata(metaData.build())
   }

   private fun getCount(): Long {
      return try {
         control!!.playList.size.toLong()
      } catch (e: RemoteException) {
         e.printStackTrace()
         0
      }
   }

   private fun getCoverBitmap(info: SongInfo) : Bitmap {
      return if (StringUtils.isReal(info.album_path)) {
         BitmapFactory.decodeFile(info.album_path)
      } else {
         BitmapFactory.decodeResource(context!!.resources, R.drawable.default_album)
      }
   }

   /**
    * 释放 MediaSession,退出播放器调用
    */
   fun release() {
      mMediaSession!!.setCallback(null)
      mMediaSession!!.isActive = false
      mMediaSession!!.release()
   }
}






















