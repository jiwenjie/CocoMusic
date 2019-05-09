package com.jiwenjie.cocomusic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.multidex.MultiDex
import android.view.WindowManager
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.utils.UnCaught
import com.pgyersdk.crash.PgyCrashManager
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
   }

   companion object {
      @SuppressLint("StaticFieldLeak")
      lateinit var contextInstance: Context
   }
}