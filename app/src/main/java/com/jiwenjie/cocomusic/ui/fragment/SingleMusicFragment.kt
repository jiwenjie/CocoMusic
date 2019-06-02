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
import kotlinx.android.synthetic.main.common_recyclerview.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/02
 *  desc:本地音乐的 fragment   localActivity -> localMusicFragment
 *  version:1.0
 */
class SingleMusicFragment : BaseFragment() {

   private lateinit var beanList: ArrayList<Music>
   private val adapter by lazy { MusicListAdapter(activity!!, beanList) }

   private var currentMusic: Music? = null

   companion object {
      private const val KEY_LOCAL_MUSIC = "key_local_music"

      @JvmStatic
      fun newInstance(beanList: ArrayList<Music>) : SingleMusicFragment {
         return SingleMusicFragment().apply {
            arguments = Bundle().apply {
               this.putParcelableArrayList(KEY_LOCAL_MUSIC, beanList)
            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      StatusBarUtil.setColor(activity, ContextCompat.getColor(activity!!, R.color.colorPrimary), 0)

      beanList = arguments!!.getParcelableArrayList(KEY_LOCAL_MUSIC)

      common_rv.adapter = adapter
      common_rv.layoutManager = LinearLayoutManager(activity)
      PlayManager.setPlayList(beanList)
       adapter.addAllData(beanList)
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

   override fun getLayoutId(): Int = R.layout.common_recyclerview
}