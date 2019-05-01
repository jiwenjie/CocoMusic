package com.jiwenjie.cocomusic.one

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jiwenjie.cocomusic.aidl.IPlayControl
import com.jiwenjie.cocomusic.bean.SongInfo
import com.jiwenjie.cocomusic.one.interfaces.ContentUpdatable
import com.jiwenjie.cocomusic.one.interfaces.OnUpdateStatusChanged
import com.jiwenjie.cocomusic.one.interfaces.ThemeChangeable
import com.jiwenjie.cocomusic.one.interfaces.ThemeEnum
import com.jiwenjie.cocomusic.one.manager.BroadcastManager
import com.jiwenjie.cocomusic.one.manager.MediaManager
import com.jiwenjie.cocomusic.one.manager.PlayNotifyManager
import com.jiwenjie.cocomusic.one.service.PlayServiceCallback
import com.jiwenjie.cocomusic.utils.PeriodicTask

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:
 *  version:1.0
 */
class BottomNavigationController(activity: Activity, mediaManager: MediaManager) : View.OnClickListener,
    PlayServiceCallback,
    ContentUpdatable,
    ThemeChangeable {

    private var activity: Activity? = null
    private var broadcastManager: BroadcastManager? = null
    private var mControl: IPlayControl? = null

    private var mediaManager: MediaManager? = null

   private var mContainer: View? = null
   private var mProgress: View? = null
   private var mProgressBG: View? = null
   private var mAlbum: ImageView? = null
   private var mName: TextView? = null
   private var mArts: TextView? = null
   private var mPlay: ImageView? = null

//   private val builder: BitmapBuilder
   private var task: PeriodicTask? = null
//   private val listViewsController: ListViewsController

   private var currentSong: SongInfo? = null
   private var hasInitData = false

   private var playNotifyManager: PlayNotifyManager? = null

   init {
      this.activity = activity
      this.mediaManager = mediaManager
      this.broadcastManager = BroadcastManager.getInstance()

      task = PeriodicTask(object : PeriodicTask.Task {
         override fun execute() {
            mContainer!!.post {
               updateProgress()
            }
         }
      }, 800)
   }

   fun initView() {
      initSelfViews()
   }

   fun initSelfViews() {
      
   }

   fun updateProgress() {

   }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun songChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(obj: Any, statusChanged: OnUpdateStatusChanged) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun noData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun themeChange(themeEnum: ThemeEnum, colors: IntArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}