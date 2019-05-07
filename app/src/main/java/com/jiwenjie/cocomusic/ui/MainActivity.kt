package com.jiwenjie.cocomusic.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.playservice.PlayManager
import com.jiwenjie.cocomusic.ui.adapter.MusicListAdapter
import com.jiwenjie.cocomusic.utils.SongLoader
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : PlayBaseActivity() {

    private val beanList by lazy { ArrayList<Music>() }
    private val adapter by lazy { MusicListAdapter(this, beanList) }

    companion object {
       fun runActivity(activity: Activity) {
           val intent = Intent(activity, MainActivity::class.java)
           activity.startActivity(intent)
       }
    }

   override fun initActivity(savedInstanceState: Bundle?) {
       super.initActivity(savedInstanceState)
       recyclerView.layoutManager = LinearLayoutManager(this)
       recyclerView.adapter = adapter
       adapter.setOnItemClickListener { position, view ->
           if (beanList.size == 0) return@setOnItemClickListener
           val id = Random().nextInt(beanList.size)
           PlayManager.play(id, beanList, Constants.PLAYLIST_LOCAL_ID)
//           adapter.notifyDataSetChanged()
       }

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
    var prePressTime = 0L

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (System.currentTimeMillis() - prePressTime > 2000) {
            prePressTime = System.currentTimeMillis()
            ToastUtils.showToast(this, "再按一次退出程序")
        } else {
            finish()
            android.os.Process.killProcess(android.os.Process.myUid())
            System.exit(0)
        }
        return false
    }

   override fun getLayoutId(): Int = R.layout.activity_main
}
