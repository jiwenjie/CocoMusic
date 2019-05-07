@file:Suppress("DEPRECATION")

package com.jiwenjie.cocomusic.play

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.view.KeyEvent
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.ui.MainActivity

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:需要在manifest文件中注册
 * Used to control headset playback.
 * Single press: pause/resume
 * Double press: next track
 * Triple press: previous track
 * Long press: voice search
 *  version:1.0
 */
class MediaButtonIntentReceiver : BroadcastReceiver() {

    companion object {
        private val TAG = MediaButtonIntentReceiver::class.java.simpleName
        private var DEBUG = true

        private val MSG_LONGPRESS_TIMEOUT = 1
        private val MSG_HEADSET_DOUBLE_CLICK_TIMEOUT = 2

        private val LONG_PRESS_DELAY = 1000
        private val DOUBLE_CLICK = 800

        private var mWakeLock: PowerManager.WakeLock? = null
        private var mClickCounter = 0
        private var mLastClickTime = 0L
        private var mDown = false
        private var mLaunched = false

        private var mHandler = @SuppressLint("HandlerLeak")
                                        object : Handler() {
            override fun handleMessage(msg: Message?) {
                when (msg!!.what) {
                    MSG_LONGPRESS_TIMEOUT -> {
                        if (DEBUG) LogUtils.v("$TAG Handling longpress timeout, launched $mLaunched")
                        if (!mLaunched) {
                            val context = msg.obj as Context
                            val intent = Intent()
                            intent.putExtra("autoshuffle", "true")
                            intent.setClass(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            context.startActivity(intent)
                            mLaunched = true
                        }
                    }
                    MSG_HEADSET_DOUBLE_CLICK_TIMEOUT -> {   // 双击时间阈值内
                        val clickCount = msg.arg1
                        val command: String?

                        if (DEBUG) LogUtils.e("$TAG Handling headset click, count = $clickCount")
                        command = when (clickCount) {
                            1 -> {
                                MusicPlayerService.CMD_TOGGLE_PAUSE
                            }
                            2 -> {
                                MusicPlayerService.CMD_NEXT
                            }
                            3 -> {
                                MusicPlayerService.CMD_PREVIOUS
                            }
                            else -> {
                                null
                            }
                        }
                        if (command != null) {
                            val context = msg.obj as Context
                            startService(context, command)
                        }
                    }
                }
            }
        }

        /**
         * 启动 musicService 并拥有 wake_lock 权限
         */
        private fun startService(context: Context, command: String) {
            val intent = Intent(context, MusicPlayerService::class.java)
            intent.action = MusicPlayerService.SERVICE_CMD
            intent.putExtra(MusicPlayerService.CMD_NAME, command)
            intent.putExtra(MusicPlayerService.FROM_MEDIA_BUTTON, true)
            context.startService(intent)
        }

        @SuppressLint("InvalidWakeLockTag")
        private fun acquireWakeLockAndSendMessage(context: Context, msg: Message, delay: Long) {
            if (mWakeLock == null) {
                val appContext = context.applicationContext
                val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Listener headset button")
                mWakeLock!!.setReferenceCounted(false) //设置无论请求多少次vakelock,都只需一次释放
            }

            mWakeLock!!.acquire(1000)   // 防止无限期 hold 住 wakelock
            mHandler.sendMessageDelayed(msg, delay)
        }

        /**
         * 如果 Handler 的消息队列中没有待处理的消息，就释放 receiver hold 住 wakelog
         */
        private fun releaseWakeLockIfHandlerIdle() {
            if (mHandler.hasMessages(MSG_LONGPRESS_TIMEOUT) || mHandler.hasMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)) {
                return
            }
            if (mWakeLock != null) {
                mWakeLock!!.release()
                mWakeLock = null
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val intentAction = intent!!.action
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intentAction) {     // 当耳机拔出时暂停播放
            if (isMusicServiceRunning(context!!)) {
                val i = Intent(context, MusicPlayerService::class.java)
                i.action = MusicPlayerService.SERVICE_CMD
                i.putExtra(MusicPlayerService.CMD_NAME, MusicPlayerService.CMD_PAUSE)
                //for multi user,when use BT play music,should start service match current user
                context.startService(i)
            }
        } else if (Intent.ACTION_MEDIA_BUTTON == intentAction) {    //耳机按钮事件
            val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

            val keycode = event.keyCode
            val action = event.action
            val eventtime = event.eventTime

            var command: String? = null
            when (keycode) {
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    command = MusicPlayerService.CMD_STOP
                }
                KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                    command = MusicPlayerService.CMD_TOGGLE_PAUSE
                }
                KeyEvent.KEYCODE_MEDIA_NEXT -> {
                    command = MusicPlayerService.CMD_NEXT
                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    command = MusicPlayerService.CMD_PREVIOUS
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    command = MusicPlayerService.CMD_PAUSE
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    command = MusicPlayerService.CMD_PLAY
                }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    command = MusicPlayerService.CMD_FORWARD
                }
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    command = MusicPlayerService.CMD_REWIND
                }
            }
            if (command != null) {
                if (action == KeyEvent.ACTION_DOWN) {
                    if (mDown) {
                        if (MusicPlayerService.CMD_TOGGLE_PAUSE == command || MusicPlayerService.CMD_PLAY == command) {
                            if (mLastClickTime != 0L && eventtime - mLastClickTime > LONG_PRESS_DELAY) {
                                acquireWakeLockAndSendMessage(context!!, mHandler.obtainMessage(MSG_LONGPRESS_TIMEOUT, context), 0)
                            }
                        }
                    } else if (event.repeatCount == 0) {
                        if (keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
                            if (eventtime - mLastClickTime >= DOUBLE_CLICK) {
                                mClickCounter = 0
                            }

                            mClickCounter++

                            mHandler.removeMessages(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT)

                            val msg = mHandler.obtainMessage(MSG_HEADSET_DOUBLE_CLICK_TIMEOUT, mClickCounter, 0, context)
                            val delay = (if (mClickCounter < 3) DOUBLE_CLICK else 0).toLong()
                            if (mClickCounter >= 3) {
                                mClickCounter = 0
                            }
                            mLastClickTime = eventtime
                            acquireWakeLockAndSendMessage(context!!, msg, delay)
                        } else {
                            startService(context!!, command)
                        }
                        mLaunched = false
                        mDown = true
                    }
                } else {
                    mHandler.removeMessages(MSG_LONGPRESS_TIMEOUT)
                    mDown = false
                }
                if (isOrderedBroadcast) {
                    abortBroadcast
                }
                releaseWakeLockIfHandlerIdle()
            }
        }
    }

    private fun isMusicServiceRunning(context: Context) : Boolean {
        var isServiceRunning = false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val maxServicesNum = 100
        val list = am.getRunningServices(maxServicesNum)
        for (info in list) {
            if (MusicPlayerService::class.java.name == info.service.className) {
                isServiceRunning = true
                break
            }
        }
        return isServiceRunning
    }
}

















