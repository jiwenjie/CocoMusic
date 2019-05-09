package com.jiwenjie.cocomusic.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.adapter.MusicListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import com.squareup.leakcanary.AnalyzedHeap.save
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : PlayBaseActivity() {

    private val beanList by lazy { ArrayList<Music>() }
    private val adapter by lazy { MusicListAdapter(this, beanList) }

    private var currentMusic: Music? = null

    companion object {
       fun runActivity(activity: Activity) {
           val intent = Intent(activity, MainActivity::class.java)
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
           val data = SongLoader.getAllLocalSongs(this@MainActivity)
           uiThread {
               adapter.addAllData(data as ArrayList<Music>)
           }
       }

       testJump.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
       }
   }

    // 双击退出程序
    var prePressTime = 0.toLong()

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - prePressTime > 2000) {
                prePressTime = System.currentTimeMillis()
                ToastUtils.showToast(this, "再按一次退出程序")
            } else {
                finish()
                android.os.Process.killProcess(android.os.Process.myUid())
                System.exit(0)
            }
            return false
        } else {
            // 点击音量键加减的时候也会响应该方法，所以在这里处理，防止点击音量键会导致应用退出
            return super.onKeyDown(keyCode, event)
        }
    }

   override fun getLayoutId(): Int = R.layout.activity_main
}
