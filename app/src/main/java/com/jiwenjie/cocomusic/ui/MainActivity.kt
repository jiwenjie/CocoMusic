package com.jiwenjie.cocomusic.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jiwenjie.cocomusic.R

class MainActivity : PlayBaseActivity() {

    companion object {
       fun runActivity(activity: Activity) {
           val intent = Intent(activity, MainActivity::class.java)
           activity.startActivity(intent)
       }
    }

   override fun initActivity(savedInstanceState: Bundle?) {

   }

   override fun getLayoutId(): Int = R.layout.activity_main
}
