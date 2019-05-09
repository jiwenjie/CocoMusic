package com.jiwenjie.cocomusic.ui.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.widget.SeekBar
import com.jiwenjie.basepart.mvp.BaseMvpFragment
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.NavigationHelper
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.event.PlayModeEvent
import com.jiwenjie.cocomusic.event.StatusChangedEvent
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.adapter.BottomMusicAdapter
import com.jiwenjie.cocomusic.ui.contract.PlayContract
import com.jiwenjie.cocomusic.ui.presenter.PlayControlPresenter
import kotlinx.android.synthetic.main.play_control_menu.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/04
 *  desc:
 *  version:1.0
 */
class PlayControlFragment : BaseMvpFragment<PlayContract.View, PlayControlPresenter>(),
        PlayContract.View,
        SeekBar.OnSeekBarChangeListener {

   private val TAG = PlayControlFragment::class.java.simpleName

   private var coverAnimator: ObjectAnimator? = null
   private var mAdapter: BottomMusicAdapter? = null
   private val musicList = ArrayList<Music>()

   override fun initFragment(savedInstanceState: Bundle?) {
      // 初始化控件
      EventBus.getDefault().register(this)
//        showLyric(FloatLyricViewManager.lyricInfo, true)
      updatePlayStatus(PlayManager.isPlaying())
      initSongList()
   }

   override fun loadData() {
      val music = PlayManager.getPlayingMusic()
      mPresenter.updateNowPlaying(music, true)
   }

   override fun setListener() {
      view?.setOnClickListener {
         NavigationHelper.navigateToPlaying(activity!!)
      }
      playPauseView.setOnClickListener {
         PlayManager.playPause()
      }
      playQueueIv.setOnClickListener {
         PlayQueueDialog.newInstance().showIt((activity as AppCompatActivity))
      }
   }

   private fun initSongList() {
      musicList.clear()
      musicList.addAll(PlayManager.getPlayList())
      if (mAdapter == null) {
         bottomPlayRcv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
         mAdapter = BottomMusicAdapter(this.activity!!, musicList)
         val snap = PagerSnapHelper()
         snap.attachToRecyclerView(bottomPlayRcv)
         bottomPlayRcv.adapter = mAdapter
         bottomPlayRcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
               super.onScrollStateChanged(recyclerView, newState)
               if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                  val manager = recyclerView.layoutManager as LinearLayoutManager
                  val first = manager.findFirstVisibleItemPosition()
                  val last = manager.findLastVisibleItemPosition()
                  LogUtils.e("Scroll", first.toString() + "-" + last)
                  if (first == last && first != PlayManager.position()) {
                     PlayManager.play(first)
                  }
               }
            }
         })
      } else {
         mAdapter?.notifyDataSetChanged()
      }
      bottomPlayRcv.scrollToPosition(PlayManager.position())
   }

   /**
    * View 实现部分
    */
   override fun setPlayingBitmap(albumArt: Bitmap?) {
   }

   override fun setPlayingBg(albumArt: Drawable?, isInit: Boolean?) {
   }

   override fun showLyric(lyric: String?, init: Boolean) {
   }

   override fun updatePlayStatus(isPlaying: Boolean) {
      if (isPlaying && !playPauseView.getPlaing()) {
         playPauseView.play()
      } else if (!isPlaying && playPauseView.getPlaing()) {
         playPauseView.pause()
      }
   }

   override fun updatePlayMode() {
   }

   override fun updateProgress(progress: Long, max: Long) {
      progressBar.progress = progress.toInt()
      progressBar.max = max.toInt()
   }

   override fun showNowPlaying(music: Music?) {
      if (music != null) {
         LogUtils.e(TAG, "展示当前播放状态" + music.album)
      }
   }

   /**
    * 有关进度条监听部分
    */
   override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
   }

   override fun onStartTrackingTouch(seekBar: SeekBar?) {
   }

   override fun onStopTrackingTouch(seekBar: SeekBar?) {
      if (PlayManager.isPlaying() || PlayManager.isPause()) {
         val progress = seekBar!!.progress
         PlayManager.seekTo(progress)
      } else {
         seekBar!!.progress = 0
      }
   }

   @Subscribe(threadMode = ThreadMode.MAIN)
   fun onPlayModeChangedEvent(event: PlayModeEvent) {
      LogUtils.e(TAG, "响应 PlayModeEvent 事件")
      updatePlayMode()
   }

   @Subscribe(threadMode = ThreadMode.MAIN)
   fun onMetaChangedEvent(event: MetaChangedEvent) {
      LogUtils.e(TAG, "响应 MetaChangedEvent 事件")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         progressBar.setProgress(PlayManager.getCurrentPosition(), true)
      } else {
         progressBar.progress = PlayManager.getCurrentPosition()
      }
      progressBar.max = PlayManager.getDuration()
   }

   @Subscribe(threadMode = ThreadMode.MAIN)
   fun onStatusChangedEvent(event: StatusChangedEvent) {
      LogUtils.e(TAG, "响应 StatusChangedEvent 事件")
      playPauseView.setLoading(!event.isPrepared)
      updatePlayStatus(event.isPlaying)
   }

   override fun onDestroy() {
      super.onDestroy()
      coverAnimator = null
      EventBus.getDefault().unregister(this)
   }

   override fun getContext(): Context = activity!!

   override fun initPresenter(): PlayControlPresenter = PlayControlPresenter(this)

   override fun getLayoutId(): Int = R.layout.play_control_menu
}














































