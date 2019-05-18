package com.jiwenjie.cocomusic.ui.model

import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.utils.RxJavaUtils
import com.jiwenjie.cocomusic.utils.SongLoader
import io.reactivex.Observable

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 -> 获取本地数据 -> model.  使用 RxJava 来实现。RxJava 使用还不是很熟悉，需要多练
 *  version:1.0
 */
class MFragmentModel : MineContract.Model {

   override fun getLocalMusicSize(): Observable<MutableList<Music>> {
      return Observable.create<MutableList<Music>> { e ->
         if (!e.isDisposed) {
            e.onNext(SongLoader.getAllLocalSongs(CocoApp.contextInstance))
            e.onComplete()
         }
      }.compose(RxJavaUtils.applyObservableAsync())
   }

   override fun getRecentOpen(): Observable<MutableList<Music>>? {
      return null
   }

   override fun managerDownload() {

   }

   override fun getMyRadio() {

   }

   override fun getMyCollect() {

   }
}