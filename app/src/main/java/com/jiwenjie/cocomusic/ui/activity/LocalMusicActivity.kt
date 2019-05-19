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
import kotlinx.android.synthetic.main.activity_local.*
import kotlin.collections.ArrayList

class LocalMusicActivity : PlayBaseActivity() {

    private lateinit var beanList: ArrayList<Music>
    private val adapter by lazy { MusicListAdapter(this, beanList) }

    private var currentMusic: Music? = null

    companion object {

        private const val KEY_BEAN_LIST = "key_bean_list"

       fun runActivity(activity: Activity, beanList: ArrayList<Music>) {
           activity.apply {
               intent.apply {
                   setClass(activity, LocalMusicActivity::class.java)
                   putExtra(KEY_BEAN_LIST, beanList)
               }
               startActivity(intent)
           }
       }
    }

   override fun initActivity(savedInstanceState: Bundle?) {
       super.initActivity(savedInstanceState)
       beanList = intent.getParcelableArrayListExtra(KEY_BEAN_LIST)

       recyclerView.adapter = adapter
       recyclerView.layoutManager = LinearLayoutManager(this)
       PlayManager.setPlayList(beanList)
       adapter.addAllData(beanList)
       adapter.setOnItemClickListener { position, view ->
           if (beanList.isNullOrEmpty() || beanList.size == 0) return@setOnItemClickListener
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

       testJump.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
       }
   }

   override fun getLayoutId(): Int = R.layout.activity_local
}
