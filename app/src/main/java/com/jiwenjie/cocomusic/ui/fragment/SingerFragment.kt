package com.jiwenjie.cocomusic.ui.fragment

import android.os.Bundle
import com.jiwenjie.basepart.views.BaseFragment
import com.jiwenjie.cocomusic.R
import kotlinx.android.synthetic.main.common_multiply_recyclerview.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/03
 *  desc:localMusic -> singerFragment (tabLayout 歌手)
 *  version:1.0
 */
class SingerFragment : BaseFragment() {

   companion object {
      @JvmStatic
      fun newInstance(): SingerFragment {
         return SingerFragment().apply {
            arguments = Bundle().apply {

            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      mLayoutStatusView = common_multipleStatusView
      mLayoutStatusView?.showLoading()
   }

   override fun getLayoutId(): Int = R.layout.common_multiply_recyclerview
}