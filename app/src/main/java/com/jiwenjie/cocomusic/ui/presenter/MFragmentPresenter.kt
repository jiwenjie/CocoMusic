package com.jiwenjie.cocomusic.ui.presenter

import android.annotation.SuppressLint
import android.database.Cursor
import com.jiwenjie.basepart.mvp.BaseMvpPresenter
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.ui.model.MFragmentModel
import com.jiwenjie.cocomusic.aidl.Song
import org.reactivestreams.Subscriber
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.provider.MediaStore
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.utils.SongLoader
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer


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
      addSubscription(
              mModel.getLocalMusicSize()
                      .subscribe({
                         mView?.showLocalMusicSize(it)
                      }, {
                         LogUtils.e(it.message.toString())
                      })
      )
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