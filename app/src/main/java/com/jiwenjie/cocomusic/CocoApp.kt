package com.jiwenjie.cocomusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.jiwenjie.cocomusic.ui.fragment.PlayControlFragment
import com.jiwenjie.cocomusic.utils.SharedPreferenceUtils
import com.jiwenjie.cocomusic.utils.UnCaught
import com.squareup.leakcanary.LeakCanary

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/22
 *  desc:
 *  version:1.0
 */
class CocoApp : Application() {

   override fun onCreate() {
      super.onCreate()
      MultiDex.install(this)

      LeakCanary.install(this)         // 内存泄漏中注册
      UnCaught.getInstance().init(this)   // 本地 crash 捕捉
//      PgyCrashManager.register()    // 新版推荐使用，注册 蒲公英
      contextInstance = this
      // 现在测试使用默认是 true，显示底部菜单控制栏
      isPlayMusic = SharedPreferenceUtils.getBooleanMethod(SharedPreferenceUtils.KEY_ISPLAY, true)
   }

   companion object {
      @SuppressLint("StaticFieldLeak")
      lateinit var contextInstance: Context

      var isPlayMusic: Boolean = false    // 标识是否播放过音乐，如果播放过则一直显示底部控制栏，否则隐藏底部播放控制栏

      // 把底部菜单栏显示出来
      fun displayBottomControl() {
         isPlayMusic = true
         SharedPreferenceUtils.setBooleanMethod(SharedPreferenceUtils.KEY_ISPLAY, isPlayMusic)
         PlayControlFragment.showBottomControl()
      }
   }
}