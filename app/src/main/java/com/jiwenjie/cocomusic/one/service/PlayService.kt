package com.jiwenjie.cocomusic.one.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Process
import com.jiwenjie.cocomusic.one.manager.BroadcastManager

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc: 该服务将在独立的进程中运行
 *        只负责对播放列表中的歌曲进行播放
 *        启动该服务的应用应确保获取了文件读取权限
 *  version:1.0
 */
class PlayService: RootService() {

    var iBinder: PlayServiceIBinder? = null

    private var broadcastManager: BroadcastManager? = null
    private var serviceQuitReceiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()

        broadcastManager = BroadcastManager.getInstance()
        iBinder = PlayServiceIBinder(this)

        ServiceInit(this, iBinder!!, mediaManager!!).start()

        iBinder!!.notifyDataIsReady()
        initBroadcast()
    }

    private fun initBroadcast() {
        serviceQuitReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (iBinder!!.status() == PlayController.STATUS_PLAYING) {
                    iBinder!!.pause()
                }
                iBinder!!.releashMediaPlayer()
                stopSelf()
            }
        }

        broadcastManager!!.registerBroadReceiver(this, serviceQuitReceiver!!, BroadcastManager.STORM_FILTER_PLAY_SERVICE_QUIT)
    }

    override fun onBind(intent: Intent?): IBinder? {
        val check = checkCallingOrSelfPermission("com.jiwenjie.cocomusic.ACCESS_PLAY_SERVICE")

        if (check == PackageManager.PERMISSION_DENIED) {
            //客户端的 onServiceConnected 方法不会被调用
            return null
        }
        return iBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        iBinder!!.releashMediaPlayer()
        unregisterReceiver()

        // 释放 MediaPlayer 时有错误，服务端始终没有彻底关闭，【退出】应用后再次打开应用，启动服务时，
        // 调用 MediaPlayer 的 reset 方法抛出异常，java.lang.illageStatExeception
        Process.killProcess(Process.myPid())
    }

    private fun unregisterReceiver() {
        if (serviceQuitReceiver != null) {
            broadcastManager!!.unregisterReceiver(this, serviceQuitReceiver!!)
        }
    }
}
























