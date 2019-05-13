package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.cocomusic.R

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/12
 *  desc:
 *  version:1.0
 */
class SearchActivity : BaseActivity() {

   companion object {
      @JvmStatic
      fun runActivity(activity: Activity) {
         val intent = Intent(activity, SearchActivity::class.java)
         activity.startActivity(intent)
      }
   }

   override fun initActivity(savedInstanceState: Bundle?) {

   }

   override fun getLayoutId(): Int = R.layout.activity_search
}