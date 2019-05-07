package com.jiwenjie.cocomusic.ui

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.SeekBar
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.BaseControlView
import com.jiwenjie.cocomusic.common.NavigationHelper
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.event.PlayModeEvent
import com.jiwenjie.cocomusic.event.StatusChangedEvent
import com.jiwenjie.cocomusic.playservice.PlayManager
import com.jiwenjie.cocomusic.playservice.PlayQueueManager.updatePlayMode
import com.jiwenjie.cocomusic.utils.CommonUtils
import com.jiwenjie.cocomusic.utils.CoverLoader
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
class PlayControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, BaseControlView {

    private val TAG = PlayControlFragment::class.java.simpleName

    private var coverAnimator: ObjectAnimator? = null
    private var currentPlayTime: Long = 0
//    private var mAdapter: BottomMusicAdapter? = null
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
        updateNowPlaying(music)
    }

    override fun onProgressUpdate(position: Long, duration: Long) {
        progressBar.progress = position.toInt()
        progressBar.max = duration.toInt()
    }

    override fun updateNowPlaying(music: Music?, isInit: Boolean?) {
        showNowPlaying(music)
        CoverLoader.loadBigImageView(context!!, music, object : CoverLoader.BitmapCallBack {
            override fun showBitmap(bitmap: Bitmap?) {
                setPlayingBitmap(bitmap)
                val blur = CommonUtils.createBlurredImageFromBitmap(bitmap!!, 12)
                setPlayingBg(blur, isInit)
            }
        })
    }

    open fun showNowPlaying(music: Music?) {
        if (music != null) {
            LogUtils.e(TAG,  "展示当前播放状态" + music.album)
        }
    }

    open fun setPlayingBg(albumArt: Drawable?, isInit: Boolean? = false) {

    }

    open fun setPlayingBitmap(albumArt: Bitmap?) {

    }

    fun updatePlayStatus(isPlaying: Boolean) {
//        if (isPlaying&&!playPauseView.isPlaying) {
//            playPauseView.play()
//        } else if (!isPlaying&&playPauseView.isPlaying){
//            playPauseView.pause()
//        }
        if (isPlaying) {
            playPauseView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.primary_material_dark))
        } else if (!isPlaying){
            playPauseView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.primary_material_light))
        }
    }

    private fun initSongList() {

    }

    override fun setListener() {
        view?.setOnClickListener {
            NavigationHelper.navigateToPlaying(activity!!)
        }
        playPauseView.setOnClickListener {
            PlayManager.playPause()
        }
    }

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
        updateNowPlaying(event.music, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(PlayManager.getCurrentPosition(),true)
        }else{
            progressBar.progress = PlayManager.getCurrentPosition()
        }
        progressBar.max = PlayManager.getDuration()
        initSongList()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStatusChangedEvent(event: StatusChangedEvent) {
        LogUtils.e(TAG, "响应 StatusChangedEvent 事件")
//        playPauseView.setLoading(!event.isPrepared)
        updatePlayStatus(event.isPlaying)
    }

    override fun onDestroy() {
        super.onDestroy()
        coverAnimator = null
        EventBus.getDefault().unregister(this)
    }

    override fun getLayoutId(): Int = R.layout.play_control_menu
}














































