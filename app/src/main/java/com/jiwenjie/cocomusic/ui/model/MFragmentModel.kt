package com.jiwenjie.cocomusic.ui.model

import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.MusicListBean
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

   override fun getLocalMusicSize(): Observable<MutableList<Music>?> {
      return RxJavaUtils
              .createObservable(SongLoader.getAllLocalSongs(CocoApp.contextInstance))
              .compose(RxJavaUtils.applyObservableAsync())
   }

   override fun getRecentOpen(): Observable<MutableList<Music>?> {
      // 这里现在只是一个占位作用，实际获取最近播放的位置应该改变
      return Observable.create<MutableList<Music>> { e ->
//         if (!e.isDisposed) {
//            SongLoader.getAllLocalSongs(CocoApp.contextInstance)?.let {
//               e.onNext(it)
//            }
//            e.onComplete()
//         }
      }.compose(RxJavaUtils.applyObservableAsync())
   }

   override fun managerDownload() {

   }

   override fun getMyRadio() {

   }

   override fun getMyCollect() {

   }

   // 目前都只是假数据，往后可以实现本地数据库存储
   override fun getCreateMusicList(): Observable<ArrayList<MusicListBean>> {
      return Observable.create<ArrayList<MusicListBean>> { e ->
         if (!e.isDisposed) {
            val beanList = ArrayList<MusicListBean>()

            for (i in 1 until 10) {
               val bean = MusicListBean()
               bean.totalSize = 20 * i
               bean.downloadSize = bean.totalSize - i * 3
               bean.ownername = "stormwenjie"
               bean.name = "皇家马德里$i"
               bean.createtime = "2019-05-28"
               beanList.add(bean)
            }

            e.onNext(beanList)
            e.onComplete()
         }
      }.compose(RxJavaUtils.applyObservableAsync())
   }

   override fun getCollectMusicList(): Observable<ArrayList<MusicListBean>> {
      return Observable.create<ArrayList<MusicListBean>> { e ->
         if (!e.isDisposed) {
            val beanList = ArrayList<MusicListBean>()

            for (i in 1 until 10) {
               val bean = MusicListBean()
               bean.totalSize = 20 * i
               bean.downloadSize = bean.totalSize - i * 3
               bean.ownername = "stormwenjie"
               bean.name = "新西兰$i"
               bean.createtime = "2019-05-28"
               beanList.add(bean)
            }

            e.onNext(beanList)
            e.onComplete()
         }
      }.compose(RxJavaUtils.applyObservableAsync())
   }
}