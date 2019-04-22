package com.jiwenjie.cocomusic.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jiwenjie.cocomusic.R

class MainActivity : AppCompatActivity() {

    companion object {
       fun runActivity(activity: Activity) {
           val intent = Intent(activity, MainActivity::class.java)
           activity.startActivity(intent)
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
