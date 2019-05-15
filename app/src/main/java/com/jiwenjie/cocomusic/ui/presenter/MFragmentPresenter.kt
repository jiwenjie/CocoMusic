package com.jiwenjie.cocomusic.ui.presenter

import com.jiwenjie.basepart.mvp.BaseMvpPresenter
import com.jiwenjie.cocomusic.ui.contract.MineContract

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 -> 我的 -> presenter
 *  version:1.0
 */
class MFragmentPresenter(view: MineContract.View) :
      BaseMvpPresenter<MineContract.View>(view), MineContract.Presenter {

   override fun getLocalMusicSize() {
   }

   override fun getRecentOpen() {
   }

   override fun managerDownload() {
   }

   override fun getMyRadio() {
   }

   override fun getMyCollect() {
   }
}