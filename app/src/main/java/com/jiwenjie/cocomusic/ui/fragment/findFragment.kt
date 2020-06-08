package com.jiwenjie.cocomusic.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.ui.adapter.ArtistListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import kotlinx.android.synthetic.main.common_multiply_recyclerview.*
import org.jetbrains.anko.doAsync

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  desc:发现 fragment
 *  version:1.0
 */
class findFragment : BaseFragment() {

   private var beanList = ArrayList<Artist>()

   private val adapter by lazy { ArtistListAdapter(activity!!, beanList) }

   companion object {
      @JvmStatic
      fun newInstance(): findFragment {
         return findFragment().apply {
            arguments = Bundle().apply {}
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      mLayoutStatusView = common_multipleStatusView
      mLayoutStatusView?.showLoading()

      doAsync {
         beanList = SongLoader.getAllArtists() as ArrayList<Artist>
         LogUtils.e("ArtistSize ${beanList.size}")
         mLayoutStatusView?.showContent()
      }

      commonRv.adapter = adapter
      commonRv.layoutManager = LinearLayoutManager(activity)
      adapter.setOnItemClickListener { position, view ->

      }
   }

   override fun getLayoutId(): Int = R.layout.common_multiply_recyclerview
}