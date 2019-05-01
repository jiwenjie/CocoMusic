@file:Suppress("DEPRECATION")

package com.jiwenjie.cocomusic.one.manager

import android.app.Activity
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.IPlayControl
import com.jiwenjie.cocomusic.bean.SongInfo
import com.jiwenjie.cocomusic.one.interfaces.ViewVisibilityChangeable
import com.jiwenjie.cocomusic.one.service.PlayController
import com.jiwenjie.cocomusic.ui.MainActivity

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:
 *  version:1.0
 */
class PlayNotifyManager(activity: Activity, control: IPlayControl) :
   ViewVisibilityChangeable {

   companion object {
      private const val PLAY_NOTIFY = "play_notify"
      private const val PLAY_NOTIFY_CODE = "play_notify_code"

      private const val PLAY_STATUS_SWITCH = 0
      private const val PLAY_NEXT = 1
      private const val PLAY_PREVIOUS = 2
      private const val PLAY_FAVORITE_STATUS_SWITCH = 3
      private const val PLAY_NOTIFY_CLOSE = 4

      private const val PLAY_NOTIFY_ID = 0x1213
   }

   private var activity: Activity? = null
   private var manager: NotificationManagerCompat? = null
   private var control: IPlayControl? = null
   private var playNotifyReceiver: PlayNotifyReceiver? = null

   init {
      this.activity = activity
      this.control = control
      this.manager = NotificationManagerCompat.from(activity)
      this.playNotifyReceiver = PlayNotifyReceiver()
   }

   private var play = false
   private var favorite: Boolean = false

   private var currentSong: SongInfo? = null

   private fun buildNotifycation(): Notification {
      val builder = NotificationCompat.Builder(activity)

      val intent = Intent(activity, MainActivity::class.java)
      val startMainActivity = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

      builder.setContentIntent(startMainActivity)
              .setTicker(activity!!.getString(R.string.app_name))
              .setSmallIcon(R.drawable.logo_small_icon)
              .setWhen(System.currentTimeMillis())
              .setOngoing(true)
              .setCustomContentView(createContentView())
              .setCustomBigContentView(createContentBigView())
              .priority = Notification.PRIORITY_HIGH

      return builder.build()
   }

   private fun createContentBigView(): RemoteViews {
      val view = RemoteViews(activity!!.packageName, R.layout.play_notify_big_view)
      setCommonView(view)
      setCommonClickPending(view)

      view.setImageViewResource(R.id.play_notify_favorite,
              if (favorite)
                 R.drawable.ic_favorite
              else
                 R.drawable.ic_favorite_border)

      val pre = Intent(PLAY_NOTIFY)
      pre.putExtra(
         PLAY_NOTIFY_CODE,
         PLAY_PREVIOUS
      )
      val p3 = PendingIntent.getBroadcast(activity,
         PLAY_PREVIOUS, pre, PendingIntent.FLAG_UPDATE_CURRENT)
      view.setOnClickPendingIntent(R.id.play_notify_pre, p3)

      val favorite = Intent(PLAY_NOTIFY)
      favorite.putExtra(
         PLAY_NOTIFY_CODE,
         PLAY_FAVORITE_STATUS_SWITCH
      )
      val p4 = PendingIntent.getBroadcast(activity,
         PLAY_FAVORITE_STATUS_SWITCH, favorite, PendingIntent.FLAG_UPDATE_CURRENT)
      view.setOnClickPendingIntent(R.id.play_notify_favorite, p4)

      return view
   }

   private fun createContentView(): RemoteViews {
      val view = RemoteViews(activity!!.packageName, R.layout.play_notify_view)
      setCommonView(view)
      setCommonClickPending(view)
      return view
   }

   // 图片，歌名，艺术家，播放按钮，下一曲按钮，关闭按钮
   private fun setCommonView(view: RemoteViews) {
      val name = currentSong!!.title
      val arts = currentSong!!.artist
      val cover = createCover(currentSong!!.album_path)

      view.setImageViewBitmap(R.id.play_notify_cover, cover)
      view.setTextViewText(R.id.play_notify_name, name)
      view.setTextViewText(R.id.play_notify_arts, "$arts-$name")

      view.setImageViewResource(
         R.id.play_notify_play,
         if (play)
            R.drawable.ic_pause
         else
            R.drawable.ic_play_arrow
      )
   }

   // 播放或暂停，下一曲，关闭
   private fun setCommonClickPending(view: RemoteViews) {
      val playOrPause = Intent(PLAY_NOTIFY)
      playOrPause.putExtra(
         PLAY_NOTIFY_CODE,
         PLAY_STATUS_SWITCH
      )
      val p1 = PendingIntent.getBroadcast(activity,
         PLAY_STATUS_SWITCH, playOrPause, PendingIntent.FLAG_UPDATE_CURRENT)
      view.setOnClickPendingIntent(R.id.play_notify_play, p1)

      val next = Intent(PLAY_NOTIFY)
      next.putExtra(
         PLAY_NOTIFY_CODE,
         PLAY_NEXT
      )
      val p2 = PendingIntent.getBroadcast(activity,
         PLAY_NEXT, next, PendingIntent.FLAG_UPDATE_CURRENT)
      view.setOnClickPendingIntent(R.id.play_notify_next, p2)

      val close = Intent(PLAY_NOTIFY)
      close.putExtra(
         PLAY_NOTIFY_CODE,
         PLAY_NOTIFY_CLOSE
      )
      val p3 = PendingIntent.getBroadcast(activity,
         PLAY_NOTIFY_CLOSE, close, PendingIntent.FLAG_UPDATE_CURRENT)
      view.setOnClickPendingIntent(R.id.play_notify_close, p3)
   }

   private fun createCover(path: String): Bitmap {
      var b = BitmapFactory.decodeFile(path)
      if (b == null) {
         b = BitmapFactory.decodeResource(activity!!.resources, R.drawable.default_song)
      }
      return b
   }

   fun updateSong(info: SongInfo?) {
      if (info == null) return

      this.currentSong = info
      try {
         play = control!!.status() == PlayController.STATUS_PLAYING
      } catch (e: Exception) {
         e.printStackTrace()
      }

      show()
   }

   fun updateFavorite() {
      if (currentSong == null) return

      try {
         play = control!!.status() == PlayController.STATUS_PLAYING
      } catch (e: Exception) {
         e.printStackTrace()
      }
      show()
   }

   override fun show() {
      if (currentSong == null) return

      val nf = buildNotifycation()
      manager!!.notify(PLAY_NOTIFY_ID, nf)
   }

   override fun hide() {
      manager!!.cancelAll()
   }

   override fun visible(): Boolean {
      return manager!!.areNotificationsEnabled()
   }

   fun initBroadcastReceivers() {
      val bd = BroadcastManager.getInstance()
      bd.registerBroadReceiver(activity!!, playNotifyReceiver!!,
         PLAY_NOTIFY
      )
   }

   fun unregisterReceiver() {
      BroadcastManager.getInstance()
         .unregisterReceiver(activity!!, playNotifyReceiver!!)
   }

   private inner class PlayNotifyReceiver : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
         val code = intent!!.getIntExtra(PLAY_NOTIFY_CODE, -1)
         if (code == -1) return

         when (code) {
            PLAY_STATUS_SWITCH -> {
               handlePlayStatusSwitch()
            }
            PLAY_NEXT -> {
               try {
                  control!!.next()
               } catch (e: Exception) {
                  e.printStackTrace()
               }
            }
            PLAY_PREVIOUS -> {
               try {
                  control!!.pre()
               } catch (e: Exception) {
                  e.printStackTrace()
               }
            }
            PLAY_FAVORITE_STATUS_SWITCH -> {
               // todo 未作
            }
            PLAY_NOTIFY_CLOSE -> {
               try {
                  if (control!!.status() == PlayController.STATUS_PLAYING) {
                     control!!.pause()
                  }
               } catch (e: Exception) {
                  e.printStackTrace()
               }
               hide()
            }
            else -> {

            }
         }
      }

      private fun handlePlayStatusSwitch() {
         try {
            if (control!!.status() == PlayController.STATUS_PLAYING) {
               control!!.pause()
            } else {
               control!!.resume()
            }
            play = !play
         } catch (e: Exception) {
            e.printStackTrace()
         }
      }
   }
}

















