package com.jiwenjie.cocomusic.one.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc:耳机线控，仅在系统版本在 KITKAT 以下有效
 *  5.0 版本之后被 MediaSessionCompat 接管
 *  version:1.0
 */
class HeadphoneWireControlReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val event = intent!!.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (event == null || event.action != KeyEvent.ACTION_UP) return
    }
}













