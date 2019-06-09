@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.jiwenjie.cocomusic.ui.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.adapter.MusicListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import kotlinx.android.synthetic.main.common_multiply_recyclerview.*
import org.jetbrains.anko.doAsync

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/02
 *  desc:本地音乐的 fragment   localActivity -> localMusicFragment
 *  version:1.0
 */
class SingleMusicFragment : BaseFragment() {

    private var size = -1
   private lateinit var beanList: ArrayList<Music>
   private val adapter by lazy { MusicListAdapter(activity!!, beanList) }

   private var currentMusic: Music? = null

   companion object {
      private const val KEY_LOCAL_MUSIC = "key_local_music"
       private var KEY_DIS_SIZE = "key_size"

      @JvmStatic
      fun newInstance(beanList: ArrayList<Music>, size: Int) : SingleMusicFragment {
         return SingleMusicFragment().apply {
            arguments = Bundle().apply {
               this.putParcelableArrayList(KEY_LOCAL_MUSIC, beanList)
                this.putInt(KEY_DIS_SIZE, size)
            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      StatusBarUtil.setColor(activity, ContextCompat.getColor(activity!!, R.color.colorPrimary), 0)
       mLayoutStatusView = common_multipleStatusView

       size = arguments!!.getInt(KEY_DIS_SIZE, -1)
       beanList = arguments!!.getParcelableArrayList(KEY_LOCAL_MUSIC)

       commonRv.adapter = adapter
       commonRv.layoutManager = LinearLayoutManager(activity)

       when (size) {
           0 -> mLayoutStatusView?.showEmpty()
           -1 -> {
               mLayoutStatusView?.showLoading()
               doAsync {
                   beanList = SongLoader.getAllLocalSongs(activity!!) as ArrayList<Music>
                   adapter.addAllData(beanList)
                   mLayoutStatusView?.showContent()
               }
           }
           else -> {
               adapter.addAllData(beanList)
               mLayoutStatusView?.showContent()
           }
       }

      PlayManager.setPlayList(beanList)

       adapter.setOnItemClickListener { position, view ->
           if (beanList.isNullOrEmpty() || beanList.size == 0) return@setOnItemClickListener
           // 处理用户重复点击一首歌曲的时候每次都重新开始播放
           if (currentMusic != null) {
               val clickMusic = beanList[position]
               if (currentMusic != clickMusic) {
                   currentMusic = clickMusic
                   PlayManager.play(position, beanList, Constants.PLAYLIST_LOCAL_ID)
               }
           } else {
               currentMusic = beanList[position]
               PlayManager.play(position, beanList, Constants.PLAYLIST_LOCAL_ID)
           }
       }
   }

   override fun getLayoutId(): Int = R.layout.common_multiply_recyclerview
}