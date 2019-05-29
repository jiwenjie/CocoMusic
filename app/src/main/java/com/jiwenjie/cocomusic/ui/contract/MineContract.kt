package com.jiwenjie.cocomusic.ui.contract

import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.MusicListBean
import com.jiwenjie.cocomusic.ui.contract.base.BaseNormalView
import io.reactivex.Observable

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:mainActivity -> mineFragment -> 首页我的契约类
 *  version:1.0
 */
interface MineContract {

   interface Model {
      fun getLocalMusicSize(): Observable<MutableList<Music>?>    // 获取本地音乐

      fun getRecentOpen(): Observable<MutableList<Music>?>       // 获取最近播放的歌曲

      fun managerDownload()   // 下载管理

      fun getMyRadio()        // 获取我的电台数量

      fun getMyCollect()      // 获取我的收藏歌曲数量

      fun getCreateMusicList() : Observable<ArrayList<MusicListBean>>     // get the musicList(歌单) for create
      fun getCollectMusicList() : Observable<ArrayList<MusicListBean>>     // get the musicList for collect
   }

   interface View: BaseNormalView {
      fun showLocalMusicSize(musicList: MutableList<Music>?)    // 获取本地音乐的数量

      fun showRecentOpen(musicList: MutableList<Music>?)        // 获取最近播放的歌曲

      fun managerDownload()   // 下载管理

      fun showMyRadio()        // 获取我的电台数量

      fun showMyCollect()      // 获取我的收藏数量

      fun showCreateMusicList(beanList: ArrayList<MusicListBean>)     // get the musicList for users create
      fun showCollectMusicList(beanList: ArrayList<MusicListBean>)    // get the musicList for users collect
   }

   interface Presenter {
      fun getLocalMusicSize()    // 获取本地音乐的数量

      fun getRecentOpen()        // 获取最近播放的歌曲

      fun managerDownload()   // 下载管理

      fun getMyRadio()        // 获取我的电台数量

      fun getMyCollect()      // 获取我的收藏数量

      fun getCreateMusicList()
      fun getCollectMusicList()
   }
}