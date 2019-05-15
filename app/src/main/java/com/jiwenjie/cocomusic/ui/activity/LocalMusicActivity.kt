package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.adapter.MusicListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import kotlinx.android.synthetic.main.activity_local.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.collections.ArrayList

class LocalMusicActivity : PlayBaseActivity() {

    private val beanList by lazy { ArrayList<Music>() }
    private val adapter by lazy { MusicListAdapter(this, beanList) }

    private var currentMusic: Music? = null

    companion object {
       fun runActivity(activity: Activity) {
           val intent = Intent(activity, LocalMusicActivity::class.java)
           activity.startActivity(intent)
       }
    }

   override fun initActivity(savedInstanceState: Bundle?) {
       super.initActivity(savedInstanceState)
       recyclerView.adapter = adapter
       recyclerView.layoutManager = LinearLayoutManager(this)
       adapter.setOnItemClickListener { position, view ->
           if (beanList.size == 0) return@setOnItemClickListener
           // 处理用户重复点击一首歌曲的时候每次都重新开始播放
           if (currentMusic != null) {
               val clickMusic = beanList[position]
               if (currentMusic != clickMusic) {
                   currentMusic = clickMusic
                   PlayManager.play(position, beanList, Constants.PLAYLIST_LOCAL_ID)
               }
           } else {
               currentMusic = beanList[position]
               PlayManager.play(position, beanList, Constants.PLAYLIST_LOCAL_ID)
           }
       }

       // 异步读取本地歌曲
       doAsync {
           val data = SongLoader.getAllLocalSongs(this@LocalMusicActivity)
           uiThread {
               adapter.addAllData(data as ArrayList<Music>)
               PlayManager.setPlayList(data)
           }
       }

       testJump.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
       }
   }

   override fun getLayoutId(): Int = R.layout.activity_local
}
