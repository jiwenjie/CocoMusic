package com.jiwenjie.cocomusic.ui.fragment

import android.os.Bundle
import com.jiwenjie.basepart.mvp.BaseMvpFragment
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.ui.presenter.MFragmentPresenter
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 -> 我的 fragment
 *  version:1.0
 */
class MineFragment : BaseMvpFragment<MineContract.View, MFragmentPresenter>(), MineContract.View {

   // 保存获取到的本地音乐，点击的时候把值作为参数传递过去
   private lateinit var localMusic: ArrayList<Music>

   companion object {
      @JvmStatic
      fun newInstance(): MineFragment {
         return MineFragment().apply {
            arguments = Bundle().apply {
               putString("", "")
            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {

   }

   override fun loadData() {
      mPresenter.getLocalMusicSize()   // 获取本地音乐的数量和内容
      mPresenter.getRecentOpen()       // 获取最近播放的歌曲
      mPresenter.getMyRadio()          // 获取我的电台
      mPresenter.getMyCollect()        // 获取我的收藏
      // todo 下载管理先不做
   }

   // get the local music
   override fun showLocalMusicSize(musicList: MutableList<Music>) {
      LogUtils.e("MusicSize: ${musicList.size}")
      localMusicSizeText.text = String.format("(%d)", musicList.size)   // 设置本地音乐的数量
      localMusic = musicList as ArrayList<Music>
   }

   // show the recently song
   override fun showRecentOpen(musicList: MutableList<Music>) {
      // 最近播放歌曲数量做一个限制，最多显示 100 首
   }

   override fun managerDownload() {

   }

   override fun showMyRadio() {

   }

   override fun showMyCollect() {

   }

   override fun initPresenter(): MFragmentPresenter = MFragmentPresenter(this)

   override fun getLayoutId(): Int = R.layout.fragment_mine
}