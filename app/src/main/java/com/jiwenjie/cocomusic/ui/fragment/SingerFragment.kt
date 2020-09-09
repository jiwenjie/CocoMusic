package com.jiwenjie.cocomusic.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.ui.adapter.ArtistListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import kotlinx.android.synthetic.main.common_multiply_recyclerview.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.runOnUiThread

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/03
 *  desc:localMusic -> singerFragment (tabLayout 歌手)
 *  version:1.0
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SingerFragment : BaseFragment() {

   private var songBeanList = ArrayList<Music>()
   private var beanList = ArrayList<Artist>()

   private val adapter by lazy { ArtistListAdapter(activity!!, beanList) }

   companion object {
      private const val KEY_LOCAL_MUSIC_Art = "key_local_music_artist"

      @JvmStatic
      fun newInstance(beanList: ArrayList<Music>): SingerFragment {
         return SingerFragment().apply {
            arguments = Bundle().apply {
               this.putParcelableArrayList(KEY_LOCAL_MUSIC_Art, beanList)
            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      mLayoutStatusView = common_multipleStatusView
      mLayoutStatusView?.showLoading()

      songBeanList = arguments!!.getParcelableArrayList(KEY_LOCAL_MUSIC_Art)

      doAsync {
         beanList = SongLoader.getAllArtists(songBeanList) as ArrayList<Artist>
         LogUtils.e("ArtistSize ${beanList.size}")
         runOnUiThread {
            mLayoutStatusView?.showContent()
         }
      }

      commonRv.adapter = adapter
      commonRv.layoutManager = LinearLayoutManager(activity)
      adapter.setOnItemClickListener { position, view ->

      }
   }

   override fun getLayoutId(): Int = R.layout.common_multiply_recyclerview
}