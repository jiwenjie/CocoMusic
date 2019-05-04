package com.jiwenjie.cocomusic.ui

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.SeekBar
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.BaseControlView
import com.jiwenjie.cocomusic.playservice.PlayManager
import java.util.ArrayList

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/04
 *  desc:
 *  version:1.0
 */
class PlayControlFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener, BaseControlView {

    private var coverAnimator: ObjectAnimator? = null
    private var currentPlayTime: Long = 0
//    private var mAdapter: BottomMusicAdapter? = null
    private val musicList = ArrayList<Music>()

    companion object {
       fun newInstance(): PlayControlFragment {
           val args = Bundle()
           val fragment = PlayControlFragment()
           fragment.arguments = args
           return fragment
       }
    }

    override fun initFragment(savedInstanceState: Bundle?) {
        // 初始化控件
//        showLyric(FloatLyricViewManager.lyricInfo, true)
        updatePlayStatus(PlayManager.isPlaying())
        initSongList()
    }

    private fun initSongList() {

    }

    override fun setListener() {
//        bottomPlayRcv
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun setPlayingBitmap(albumArt: Bitmap?) {
    }

    override fun setPlayingBg(albumArt: Drawable?, isInit: Boolean?) {
    }

    override fun showLyric(lyric: String?, init: Boolean) {
    }

    override fun updatePlayStatus(isPlaying: Boolean) {
//        if (isPlaying&&!playPauseView.isPlaying) {
//            playPauseView.play()
//        } else if (!isPlaying&&playPauseView.isPlaying){
//            playPauseView.pause()
//        }
    }

    override fun updatePlayMode() {
    }

    override fun updateProgress(progress: Long, max: Long) {
    }

    override fun showNowPlaying(music: Music?) {
    }

    override fun getLayoutId(): Int = R.layout.play_control_menu
}














































