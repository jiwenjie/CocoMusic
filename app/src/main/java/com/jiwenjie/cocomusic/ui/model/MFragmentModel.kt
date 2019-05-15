package com.jiwenjie.cocomusic.ui.model

import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.utils.SongLoader
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 ->
 *  version:1.0
 */
class MFragmentModel : MineContract.Model {

   override fun getLocalMusicSize(): MutableList<Music>? {
      var data: MutableList<Music>
      doAsync {
         data = SongLoader.getAllLocalSongs(CocoApp.contextInstance)
         return@doAsync
      }
      return null
   }

   override fun getRecentOpen(): MutableList<Music>? {
      return null
   }

   override fun managerDownload() {

   }

   override fun getMyRadio() {

   }

   override fun getMyCollect() {

   }
}