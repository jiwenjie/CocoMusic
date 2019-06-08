package com.jiwenjie.cocomusic.ui.presenter

import android.annotation.SuppressLint
import android.os.Handler
import com.jiwenjie.basepart.mvp.BaseMvpPresenter
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.ui.model.MFragmentModel
import com.jiwenjie.basepart.utils.LogUtils


/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 -> 我的 -> presenter
 *  version:1.0
 */
class MFragmentPresenter(view: MineContract.View) :
        BaseMvpPresenter<MineContract.View>(view), MineContract.Presenter {

   private val mModel by lazy { MFragmentModel() }

   @SuppressLint("CheckResult")
   override fun getLocalMusicSize() {
      // 展示进度条
      // 暂时作为缓冲
      mView?.showLoading()
           // 模拟获取数据，否则速度太快
       addSubscription(mModel.getLocalMusicSize()
           .subscribe({
               LogUtils.e("MusicSize: ${it?.size}")
               mView?.dismissProgress()
               mView?.showLocalMusicSize(it)
           }, {
               LogUtils.e(it.message.toString())
           }))

   }

   override fun getRecentOpen() {
   }

   override fun managerDownload() {
   }

   override fun getMyRadio() {
   }

   override fun getMyCollect() {
   }

   /** 获取创建的歌单列表 **/
   override fun getCreateMusicList() {
      mView?.showLoading()
      addSubscription(
              mModel.getCreateMusicList()
                      .subscribe({
//                         mView?.dismissProgress()
                         mView?.showCreateMusicList(it)
                      }, {
                         LogUtils.e(it.message.toString())
                      }))
   }

   override fun getCollectMusicList() {
      mView?.showLoading()
      addSubscription(
              mModel.getCreateMusicList()
                      .subscribe({
//                         mView?.dismissProgress()
                         mView?.showCollectMusicList(it)
                      }, {
                         LogUtils.e(it.message.toString())
                      }))
   }
}