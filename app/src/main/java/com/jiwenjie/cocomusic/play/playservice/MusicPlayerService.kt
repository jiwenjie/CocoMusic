package com.jiwenjie.cocomusic.play.playservice

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.*
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.common.Extras
import com.jiwenjie.cocomusic.common.PlayProgressListener
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.event.PlaylistEvent
import com.jiwenjie.cocomusic.event.StatusChangedEvent
import com.jiwenjie.cocomusic.play.AudioAndFocusManager
import com.jiwenjie.cocomusic.play.IMusicServiceStub
import com.jiwenjie.cocomusic.play.MediaSessionManager
import com.jiwenjie.cocomusic.ui.PlayerDetailActivity
import com.jiwenjie.cocomusic.utils.*
import com.jiwenjie.cocomusic.widget.StandardWidget
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:播放service
 *  version:1.0
 */
class MusicPlayerService : Service() {

   companion object {
      private const val TAG = "MusicPlayerService"

      const val ACTION_SERVICE = "com.jiwenjie.music_lake.service"// 广播标志
      // 通知栏
      const val ACTION_PREV = "com.jiwenjie.music_lake.notify.prev"// 上一首广播标志
      const val ACTION_NEXT = "com.jiwenjie.music_lake.notify.next"// 下一首广播标志
      const val ACTION_PLAY_PAUSE = "com.jiwenjie.music_lake.notify.play_state"// 播放暂停广播
      const val ACTION_CLOSE = "com.jiwenjie.music_lake.notify.close"// 播放暂停广播
      const val ACTION_IS_WIDGET = "ACTION_IS_WIDGET"// 播放暂停广播

      const val ACTION_LYRIC = "com.jiwenjie.music_lake.notify.lyric"// 播放暂停广播

      const val PLAY_STATE_CHANGED = "com.jiwenjie.music_lake.play_state"// 播放暂停广播

      const val PLAY_STATE_LOADING_CHANGED = "com.jiwenjie.music_lake.play_state_loading"// 播放loading

      const val DURATION_CHANGED = "com.jiwenjie.music_lake.duration"// 播放时长

      const val TRACK_ERROR = "com.jiwenjie.music_lake.error"
      const val SHUTDOWN = "com.jiwenjie.music_lake.shutdown"
      const val REFRESH = "com.jiwenjie.music_lake.refresh"

      const val PLAY_QUEUE_CLEAR = "com.jiwenjie.music_lake.play_queue_clear" //清空播放队列
      const val PLAY_QUEUE_CHANGE = "com.jiwenjie.music_lake.play_queue_change" //播放队列改变

      const val META_CHANGED = "com.jiwenjie.music_lake.metachanged"//状态改变(歌曲替换)
      const val SCHEDULE_CHANGED = "com.jiwenjie.music_lake.schedule"//定时广播

      const val CMD_TOGGLE_PAUSE = "toggle_pause"//按键播放暂停
      const val CMD_NEXT = "next"//按键下一首
      const val CMD_PREVIOUS = "previous"//按键上一首
      const val CMD_PAUSE = "pause"//按键暂停
      const val CMD_PLAY = "play"//按键播放
      const val CMD_STOP = "stop"//按键停止
      const val CMD_FORWARD = "forward"//按键停止
      const val CMD_REWIND = "reward"//按键停止
      const val SERVICE_CMD = "cmd_service"//状态改变
      const val FROM_MEDIA_BUTTON = "media"//状态改变
      const val CMD_NAME = "name"//状态改变
      const val UNLOCK_DESKTOP_LYRIC = "unlock_lyric" //音量改变增加

      const val TRACK_WENT_TO_NEXT = 2 //下一首
      const val RELEASE_WAKELOCK = 3 //播放完成
      const val TRACK_PLAY_ENDED = 4 //播放完成
      const val TRACK_PLAY_ERROR = 5 //播放出错

      const val PREPARE_ASYNC_UPDATE = 7 //PrepareAsync装载进程
      const val PLAYER_PREPARED = 8 //mediaplayer准备完成

      const val AUDIO_FOCUS_CHANGE = 12 //音频焦点改变
      const val VOLUME_FADE_DOWN = 13 //音量改变减少
      const val VOLUME_FADE_UP = 14 //音量改变增加

      // 进度改变监听
      private val listenerList = java.util.ArrayList<PlayProgressListener>()

      fun addProgressListener(listener: PlayProgressListener) {
         listenerList.add(listener)
      }

      fun removeProgressListener(listener: PlayProgressListener) {
         listenerList.remove(listener)
      }

      private var instance: MusicPlayerService? = null

      fun getInstance(): MusicPlayerService? {
         return instance
      }
   }

   private val NOTIFICATION_ID = 0x123
   private var mNotificationPostTime: Long = 0
   private var mServiceStartId = -1

   /**
    * 错误次数，超过最大错误次数，自动停止播放
    */
   private var playErrorTimes = 0
   private val MAX_ERROR_TIMES = 5

   private val DEBUG = true

   private var mPlayer: MusicPlayerEngine? = null
   lateinit var mWakeLock: PowerManager.WakeLock
   private var powerManager: PowerManager? = null

   var mPlayingMusic: Music? = null
   private var mPlayQueue: MutableList<Music>? = java.util.ArrayList()
   private val mHistoryPos = java.util.ArrayList<Int>()
   private var mPlayingPos = -1
   private val mNextPlayPos = -1
   private var mPlaylistId = Constants.PLAYLIST_QUEUE_ID

   //广播接收者
   internal lateinit var mServiceReceiver: ServiceReceiver
   internal lateinit var mHeadsetReceiver: HeadsetReceiver
   internal lateinit var mStandardWidget: StandardWidget
   internal lateinit var mHeadsetPlugInReceiver: HeadsetPlugInReceiver
   internal lateinit var intentFilter: IntentFilter

   // private var mFloatLyricViewManager: FloatLyricViewManager? = null

   private var mediaSessionManager: MediaSessionManager? = null
   private var audioAndFocusManager: AudioAndFocusManager? = null

   private var mNotificationManager: NotificationManager? = null
   private var mNotificationBuilder: NotificationCompat.Builder? = null
   private var mNotification: Notification? = null
   private val mBindStub = IMusicServiceStub(this)
   private var isRunningForeground = false
   private var isMusicPlaying = false
   //暂时失去焦点，会再次回去音频焦点
   private var mPausedByTransientLossOfFocus = false

   var totalTime = 0

   internal var mServiceInUse = false
   //工作线程和Handler
   private var mHandler: MusicPlayerHandler? = null
   private var mWorkThread: HandlerThread? = null
   //主线程Handler
   private var mMainHandler: Handler? = null

   private var showLyric: Boolean = false

   private var disposable: Disposable? = null

   @Suppress("DEPRECATED_IDENTITY_EQUALS")
   inner class MusicPlayerHandler(service: MusicPlayerService, looper: Looper) : Handler(looper) {
      private val mService: WeakReference<MusicPlayerService> = WeakReference(service)
      private var mCurrentVolume = 1.0f

      override fun handleMessage(msg: Message) {
         super.handleMessage(msg)
         val service = mService.get()
         synchronized(mService) {
            when (msg.what) {
               VOLUME_FADE_DOWN -> {
                  mCurrentVolume -= 0.05f
                  if (mCurrentVolume > 0.2f) {
                     sendEmptyMessageDelayed(VOLUME_FADE_DOWN, 10)
                  } else {
                     mCurrentVolume = 0.2f
                  }
                  service!!.mPlayer!!.setVolume(mCurrentVolume)
               }
               VOLUME_FADE_UP -> {
                  mCurrentVolume += 0.01f
                  if (mCurrentVolume < 1.0f) {
                     sendEmptyMessageDelayed(VOLUME_FADE_UP, 10)
                  } else {
                     mCurrentVolume = 1.0f
                  }
                  service!!.mPlayer!!.setVolume(mCurrentVolume)
               }
               TRACK_WENT_TO_NEXT -> {//mplayer播放完毕切换到下一首
                  // service.setAndRecordPlayPos(service.mNextPlayPos);
                  mMainHandler!!.post { service!!.next(true) }
               }
               TRACK_PLAY_ENDED -> {//mPlayer播放完毕且暂时没有下一首
                   if (PlayQueueManager.getPlayModeId() === PlayQueueManager.PLAY_MODE_REPEAT) {
                     service!!.seekTo(0, false)
                     mMainHandler!!.post { service.play() }
                     } else {
                        mMainHandler!!.post { service?.next(true) }
                     }
               }
               TRACK_PLAY_ERROR -> {           //mPlayer播放错误
                  LogUtils.e(TAG, msg.obj.toString() + "---")
                  playErrorTimes++
                  if (playErrorTimes < MAX_ERROR_TIMES) {
                     mMainHandler!!.post { service?.next(true) }
                  } else {
                     mMainHandler!!.post { service?.pause() }
                  }
               }
               RELEASE_WAKELOCK -> {            //释放电源锁
                service!!.mWakeLock.release()
               }
               PREPARE_ASYNC_UPDATE -> {
                  val percent = msg.obj as Int
                  LogUtils.e(TAG, "Loading ... $percent")
                  notifyChange(PLAY_STATE_LOADING_CHANGED)
               }
               PLAYER_PREPARED ->
                  //执行prepared之后 准备完成，更新总时长
                  notifyChange(PLAY_STATE_CHANGED)
               AUDIO_FOCUS_CHANGE -> when (msg.arg1) {
                  AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {     //暂时失去焦点
                     if (service!!.isPlaying()) {
                        mPausedByTransientLossOfFocus = msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                     }
                     mMainHandler!!.post { service.pause() }
                  }
                  AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                     removeMessages(VOLUME_FADE_UP)
                     sendEmptyMessage(VOLUME_FADE_DOWN)
                  }
                  AudioManager.AUDIOFOCUS_GAIN -> {          //重新获取焦点
                     //重新获得焦点，且符合播放条件，开始播放
                     if (!service!!.isPlaying() && mPausedByTransientLossOfFocus) {
                        mPausedByTransientLossOfFocus = false
                        mCurrentVolume = 0f
                        service.mPlayer!!.setVolume(mCurrentVolume)
                        mMainHandler!!.post { service.play() }
                     } else {
                        removeMessages(VOLUME_FADE_DOWN)
                        sendEmptyMessage(VOLUME_FADE_UP)
                     }
                  }
                  else -> {
                  }
               }
               else -> {
               }
            }           // service.updateCursor(service.mPlayQueue.get(service.mPlayPos).mId);
                        // service.bumpSongCount(); //更新歌曲的播放次数
         }
      }
   }

   override fun onCreate() {
      super.onCreate()
      LogUtils.e(TAG, "onCreate")
      instance = this
      disposable = updateMusicSeek()
      //初始化广播
      initReceiver()
      //初始化参数
      initConfig()
      //初始化电话监听服务
      initTelephony()
      //初始化通知
      initNotify()
      //初始化音乐播放服务
      initMediaPlayer()
   }

   /**
    * 通过 RxJava 来异步更新底部控制栏的歌曲进度
    */
   private fun updateMusicSeek(): Disposable {
      return Observable
         .interval(500, TimeUnit.MILLISECONDS)
         .subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe { v ->
            for (i in listenerList.indices) {
               LogUtils.e(TAG, "currentPosition ${getCurrentPosition()} : duration ${getDuration()}")
               listenerList[i].onProgressUpdate(getCurrentPosition(), getDuration())
            }
         }
   }

   /**
    * 参数配置，AudioManager、锁屏
    */
   @SuppressLint("InvalidWakeLockTag")
   private fun initConfig() {
      //初始化主线程Handler
      mMainHandler = Handler(Looper.getMainLooper())
      PlayQueueManager.getPlayModeId()

      //初始化工作线程
      mWorkThread = HandlerThread("MusicPlayerThread")
      mWorkThread!!.start()

      mHandler = MusicPlayerHandler(this, mWorkThread!!.looper)

      //电源键
      powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
      mWakeLock = powerManager!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PlayerWakelockTag")

//        mFloatLyricViewManager = FloatLyricViewManager(this)

      //初始化和设置MediaSessionCompat
      mediaSessionManager = MediaSessionManager(this, mBindStub, mMainHandler!!)
      audioAndFocusManager = AudioAndFocusManager(this, mHandler!!)
   }


   /**
    * 释放通知栏;
    */
   private fun releaseServiceUiAndStop() {
      if (isPlaying() || mHandler!!.hasMessages(TRACK_PLAY_ENDED)) {
         return
      }

      if (DEBUG) LogUtils.e(TAG, "Nothing is playing anymore, releasing notification")
      cancelNotification()

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
         mediaSessionManager!!.release()

      if (!mServiceInUse) {
         savePlayQueue(false)
         stopSelf(mServiceStartId)
      }
   }

   /**
    * 重新加载当前进度
    */
   fun reloadPlayQueue() {
      mPlayQueue!!.clear()
      mHistoryPos.clear()
//      mPlayQueue = PlayQueueLoader.getPlayQueue()   todo 暂时不通过数据库来获取
      doAsync {
         val data = SongLoader.getAllLocalSongs(CocoApp.contextInstance)
         uiThread {
            mPlayQueue= data
         }
      }
      mPlayingPos = SPUtils.playPosition
      if (mPlayingPos >= 0 && mPlayingPos < mPlayQueue!!.size) {
         mPlayingMusic = mPlayQueue!![mPlayingPos]
         updateNotification(false)
         seekTo(SPUtils.position, true)
         notifyChange(META_CHANGED)
      }
      notifyChange(PLAY_QUEUE_CHANGE)
   }

   /**
    * 初始化电话监听服务
    */
   private fun initTelephony() {
      val telephonyManager = this
              .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager// 获取电话通讯服务
      telephonyManager.listen(ServicePhoneStateListener(),
              PhoneStateListener.LISTEN_CALL_STATE)// 创建一个监听对象，监听电话状态改变事件
   }

   /**
    * 初始化音乐播放服务
    */
   private fun initMediaPlayer() {
      mPlayer = MusicPlayerEngine(this)
      mPlayer!!.setHandler(mHandler!!)
      reloadPlayQueue()
   }

   /**
    * 初始化广播
    */
   private fun initReceiver() {
      //实例化过滤器，设置广播
      intentFilter = IntentFilter(ACTION_SERVICE)
      mServiceReceiver = ServiceReceiver()
      mHeadsetReceiver = HeadsetReceiver()
      mStandardWidget = StandardWidget()
      mHeadsetPlugInReceiver = HeadsetPlugInReceiver()
      intentFilter.addAction(ACTION_NEXT)
      intentFilter.addAction(ACTION_PREV)
      intentFilter.addAction(META_CHANGED)
      intentFilter.addAction(SHUTDOWN)
      intentFilter.addAction(ACTION_PLAY_PAUSE)
      //注册广播
      registerReceiver(mServiceReceiver, intentFilter)
      registerReceiver(mHeadsetReceiver, intentFilter)
      registerReceiver(mHeadsetPlugInReceiver, intentFilter)
      registerReceiver(mStandardWidget, intentFilter)
   }

   /**
    * 启动Service服务，执行onStartCommand
    *
    * @param intent
    * @param flags
    * @param startId
    * @return
    */
   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      LogUtils.e(TAG, "Got new intent $intent, startId = $startId")
      mServiceStartId = startId
      mServiceInUse = true
      if (intent != null) {
         val action = intent.action
         if (SHUTDOWN == action) {
            LogUtils.e("即将关闭音乐播放器")
            //                mShutdownScheduled = true;
            releaseServiceUiAndStop()
            return Service.START_NOT_STICKY
         }
         handleCommandIntent(intent)
      }
      return Service.START_NOT_STICKY
   }

   /**
    * 绑定Service
    *
    * @param intent
    * @return
    */
   override fun onBind(intent: Intent): IBinder? {
      return mBindStub
   }

   private fun setAndRecordPlayPos(mNextPlayPos: Int) {
      mPlayingPos = mNextPlayPos
   }

   /**
    * 下一首
    */
   fun next(isAuto: Boolean?) {
      synchronized(this) {
         mPlayingPos = PlayQueueManager.getNextPostion(isAuto, mPlayQueue!!.size, mPlayingPos)
         LogUtils.e(TAG, "next: $mPlayingPos")
         stop(false)
         playCurrentAndNext()
      }
   }

   /**
    * 上一首
    */
   fun prev() {
      synchronized(this) {
         mPlayingPos = PlayQueueManager.getPreviousPosition(mPlayQueue!!.size, mPlayingPos)
         LogUtils.e(TAG, "prev: $mPlayingPos")
         stop(false)
         playCurrentAndNext()
      }
   }

   /**
    * 播放当前歌曲
    */
   private fun playCurrentAndNext() {
      synchronized(this) {
         LogUtils.e("mPlayingPos = $mPlayingPos mPlayQueue!!.size = ${mPlayQueue!!.size}")
         if (mPlayingPos >= mPlayQueue!!.size || mPlayingPos < 0) {
            return
         }
         mPlayingMusic = mPlayQueue!![mPlayingPos]
         notifyChange(META_CHANGED)
         LogUtils.e(TAG, "playingSongInfo:" + mPlayingMusic!!.toString())
         if (mPlayingMusic!!.uri == null || mPlayingMusic!!.type != Constants.LOCAL
                 || mPlayingMusic!!.uri.equals("") || mPlayingMusic!!.uri.equals("null")) {
//                ApiManager.request(MusicApi.INSTANCE.getMusicInfo(mPlayingMusic), object : RequestCallBack<Music>() {
//                    fun success(result: Music) {
//                        LogUtil.e(TAG, "-----" + result.toString())
//                        mPlayingMusic = result
//                        saveHistory()
//                        isMusicPlaying = true
//                        playErrorTimes = 0
//                        mPlayer!!.setDataSource(mPlayingMusic!!.getUri())
//                    }
//
//                    fun error(msg: String) {
//                        ToastUtils.show(msg)
//                    }
//                })
         }
         saveHistory()
         mHistoryPos.add(mPlayingPos)
         if (mPlayingMusic!!.uri != null) {
            if (!mPlayingMusic!!.uri!!.startsWith(Constants.IS_URL_HEADER) && !FileUtils.exists(mPlayingMusic!!.uri!!)) {
               isAbnormalPlay()
            } else {
               playErrorTimes = 0
               mPlayer!!.setDataSource(mPlayingMusic!!.uri!!)
            }
         }

         mediaSessionManager!!.updateMetaData(mPlayingMusic)

         audioAndFocusManager!!.requestAudioFocus()
         updateNotification(false)

         val intent = Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION)
         intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
         intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
         sendBroadcast(intent)

         if (mPlayer!!.isInitialized()) {
            mHandler!!.removeMessages(VOLUME_FADE_DOWN)
            mHandler!!.sendEmptyMessage(VOLUME_FADE_UP) //组件调到正常音量
            isMusicPlaying = true
            //                notifyChange(PLAY_STATE_CHANGED);
         }
      }
   }

   /**
    * 异常播放，自动切换下一首
    */
   private fun isAbnormalPlay() {
      if (playErrorTimes > MAX_ERROR_TIMES) {
         pause()
      } else {
         playErrorTimes++
         ToastUtils.showToast(CocoApp.contextInstance, "播放地址异常，自动切换下一首")
         next(false)
      }
   }

   /**
    * 停止播放
    *
    * @param remove_status_icon
    */
   fun stop(remove_status_icon: Boolean) {
      if (mPlayer != null && mPlayer!!.isInitialized()) {
         mPlayer!!.stop()
      }

      if (remove_status_icon) {
         cancelNotification()
      }

      if (remove_status_icon) {
         isMusicPlaying = false
      }
   }

   /**
    * 获取下一首位置
    *
    * @return
    */
   private fun getNextPosition(isAuto: Boolean?): Int {
      val playModeId = PlayQueueManager.getPlayModeId()
      if (mPlayQueue == null || mPlayQueue!!.isEmpty()) {
         return -1
      }
      if (mPlayQueue!!.size == 1) {
         return 0
      }
      if (playModeId == PlayQueueManager.PLAY_MODE_REPEAT && isAuto!!) {
         return if (mPlayingPos < 0) {
            0
         } else {
            mPlayingPos
         }
      } else if (playModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
         return Random().nextInt(mPlayQueue!!.size)
      } else {
         if (mPlayingPos == mPlayQueue!!.size - 1) {
            return 0
         } else if (mPlayingPos < mPlayQueue!!.size - 1) {
            return mPlayingPos + 1
         }
      }
      return mPlayingPos
   }

   /**
    * 获取上一首位置
    *
    * @return
    */
   private fun getPreviousPosition(): Int {
      val playModeId = PlayQueueManager.getPlayModeId()
      if (mPlayQueue == null || mPlayQueue!!.isEmpty()) {
         return -1
      }
      if (mPlayQueue!!.size == 1) {
         return 0
      }
      if (playModeId == PlayQueueManager.PLAY_MODE_REPEAT) {
         if (mPlayingPos < 0) {
            return 0
         }
      } else if (playModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
         mPlayingPos = Random().nextInt(mPlayQueue!!.size)
         return Random().nextInt(mPlayQueue!!.size)
      } else {
         if (mPlayingPos == 0) {
            return mPlayQueue!!.size - 1
         } else if (mPlayingPos > 0) {
            return mPlayingPos - 1
         }
      }
      return mPlayingPos
   }

   /**
    * 根据位置播放音乐
    *
    * @param position
    */
   fun playMusic(position: Int) {
      LogUtils.e("MusicPlayerService 根据位置播放音乐")
      if (position >= mPlayQueue!!.size || position == -1) {
         mPlayingPos = PlayQueueManager.getNextPostion(true, mPlayQueue!!.size, position)
      } else {
         mPlayingPos = position
      }
      if (mPlayingPos == -1)
         return
      playCurrentAndNext()
   }

   /**
    * 音乐播放
    */
   fun play() {
      LogUtils.e("音乐播放按钮")
      if (mPlayer!!.isInitialized()) {
         mPlayer!!.start()
         isMusicPlaying = true
         notifyChange(PLAY_STATE_CHANGED)
         audioAndFocusManager!!.requestAudioFocus()
         mHandler!!.removeMessages(VOLUME_FADE_DOWN)
         mHandler!!.sendEmptyMessage(VOLUME_FADE_UP) //组件调到正常音量

         updateNotification(true)
      } else {
         playCurrentAndNext()
      }
   }

   fun getAudioSessionId(): Int {
      synchronized(this) {
         return mPlayer!!.getAudioSessionId()
      }
   }

   /**
    * 【在线音乐】加入播放队列并播放音乐
    *
    * @param music
    */
   fun play(music: Music?) {
      if (music == null) return
      if (mPlayingPos == -1 || mPlayQueue!!.size == 0) {
         mPlayQueue!!.add(music)
         mPlayingPos = 0
      } else if (mPlayingPos < mPlayQueue!!.size) {
         mPlayQueue!!.add(mPlayingPos, music)
      } else {
         mPlayQueue!!.add(mPlayQueue!!.size, music)
      }
      LogUtils.e(music.toString())
      mPlayingMusic = music
      playCurrentAndNext()
   }

   /**
    * 下一首播放
    *
    * @param music 设置的歌曲
    */
   fun nextPlay(music: Music) {
      if (mPlayQueue!!.size == 0) {
         play(music)
      } else if (mPlayingPos < mPlayQueue!!.size) {
         mPlayQueue!!.add(mPlayingPos + 1, music)
      }
   }

   /**
    * 切换歌单播放
    * 1、歌单不一样切换
    */
   fun play(musicList: List<Music>, id: Int, pid: String) {
      if (musicList.size <= id) return
      if (mPlaylistId != pid || mPlayQueue!!.size == 0 || mPlayQueue!!.size != musicList.size) {
         setPlayQueue(musicList)
         mPlaylistId = pid
      }
      mPlayingPos = id
      playCurrentAndNext()
   }


   /**
    * 播放暂停
    */
   fun playPause() {
      if (isPlaying()) {
         pause()
      } else {
         if (mPlayer!!.isInitialized()) {
            play()
         } else {
            isMusicPlaying = true
            playCurrentAndNext()
         }
      }
   }

   /**
    * 暂停播放
    */
   fun pause() {
      if (DEBUG) LogUtils.d("Pausing playback")
      synchronized(this) {
         mHandler!!.removeMessages(VOLUME_FADE_UP)
         mHandler!!.sendEmptyMessage(VOLUME_FADE_DOWN)

         if (isPlaying()) {
            isMusicPlaying = false
            notifyChange(PLAY_STATE_CHANGED)
            updateNotification(true)
            val task = object : TimerTask() {
               override fun run() {
                  val intent = Intent(
                          AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
                  intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
                  intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
                  sendBroadcast(intent) //由系统接收,通知系统audio_session将关闭,不再使用音效

                  mPlayer!!.pause()
               }
            }
            val timer = Timer()
            timer.schedule(task, 200)
         }
      }
   }

   /**
    * 是否正在播放音乐
    *
    * @return 是否正在播放音乐
    */
   fun isPlaying(): Boolean {
      return isMusicPlaying
   }

   /**
    * 跳到输入的进度
    */
   fun seekTo(pos: Long, isInit: Boolean) {
      LogUtils.e("seekTo $pos")
      if (mPlayer != null && mPlayer!!.isInitialized() && mPlayingMusic != null) {
         mPlayer!!.seek(pos)
         LogUtils.e("seekTo 成功")
      } else if (isInit) {
         //            playCurrentAndNext();
         //            mPlayer.seek(pos);
         //            mPlayer.pause();
         LogUtils.e("seekTo 失败")
      }
   }

   override fun onUnbind(intent: Intent): Boolean {
      LogUtils.e("$TAG onUnbind")
      mServiceInUse = false
      savePlayQueue(false)

      releaseServiceUiAndStop()
      stopSelf(mServiceStartId)
      return true
   }

   /**
    * 保存播放队列
    *
    * @param full 是否存储
    */
   private fun savePlayQueue(full: Boolean) {
      if (full) {
//            PlayQueueLoader.INSTANCE.updateQueue(mPlayQueue)
      }
      if (mPlayingMusic != null) {
         //保存歌曲id
         SPUtils.saveCurrentSongId(mPlayingMusic!!.mid!!)
      }
      //保存歌曲id
      SPUtils.savePosition(mPlayingPos.toLong())
      //保存歌曲进度
      SPUtils.savePosition(getCurrentPosition())

      LogUtils.e("save 保存歌曲id=" + mPlayingPos + " 歌曲进度= " + getCurrentPosition())
      notifyChange(PLAY_QUEUE_CHANGE)
   }


   private fun saveHistory() {
//        PlayHistoryLoader.INSTANCE.addSongToHistory(mPlayingMusic)
      savePlayQueue(false)
   }

   /**
    * 获取正在播放的歌曲[本地|网络]
    */
   fun removeFromQueue(position: Int) {
      try {
         LogUtils.e(position.toString() + "---" + mPlayingPos + "---" + mPlayQueue!!.size)
         if (position == mPlayingPos) {
            mPlayQueue!!.removeAt(position)
            if (mPlayQueue!!.size == 0) {
               clearQueue()
            } else {
               playMusic(position)
            }
         } else if (position > mPlayingPos) {
            mPlayQueue!!.removeAt(position)
         } else if (position < mPlayingPos) {
            mPlayQueue!!.removeAt(position)
            mPlayingPos -= 1
         }
         notifyChange(PLAY_QUEUE_CLEAR)
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }

   /**
    * 获取正在播放的歌曲[本地|网络]
    */
   fun clearQueue() {
      mPlayingMusic = null
      isMusicPlaying = false
      mPlayingPos = -1
      mPlayQueue!!.clear()
      mHistoryPos.clear()
      savePlayQueue(true)
      stop(true)
      notifyChange(META_CHANGED)
      notifyChange(PLAY_STATE_CHANGED)
      notifyChange(PLAY_QUEUE_CLEAR)
   }

   /**
    * 获取正在播放进度
    */
   fun getCurrentPosition(): Long {
      return if (mPlayer != null && mPlayer!!.isInitialized()) {
         mPlayer!!.position()
      } else {
         0
      }
   }

   /**
    * 获取总时长
    */
   fun getDuration(): Long {
      return if (mPlayer != null && mPlayer!!.isInitialized() && mPlayer!!.isPrepared()) {
         mPlayer!!.getDuration()
      } else 0
   }

   /**
    * 是否准备播放
    *
    * @return
    */
   fun isPrepared(): Boolean {
      return if (mPlayer != null) {
         mPlayer!!.isPrepared()
      } else false
   }

   /**
    * 发送更新广播
    *
    * @param what 发送更新广播
    */
   private fun notifyChange(what: String) {
      if (DEBUG) LogUtils.d("$TAG notifyChange: what = $what")
      when (what) {
         META_CHANGED -> {
//                mFloatLyricViewManager!!.loadLyric(mPlayingMusic)
            updateWidget(META_CHANGED)
            //                notifyChange(PLAY_STATE_CHANGED);
            EventBus.getDefault().post(MetaChangedEvent(mPlayingMusic))
         }
         PLAY_STATE_CHANGED -> {
            updateWidget(ACTION_PLAY_PAUSE)
            mediaSessionManager!!.updatePlaybackState()
            EventBus.getDefault().post(StatusChangedEvent(isPrepared(), isPlaying()))
         }
         PLAY_QUEUE_CLEAR, PLAY_QUEUE_CHANGE -> EventBus.getDefault().post(PlaylistEvent(Constants.PLAYLIST_QUEUE_ID, null))
      }
      //  case PLAY_STATE_LOADING_CHANGED:
      //  EventBus.getDefault().post(new StatusChangedEvent(false, isPlaying()));
      //  break;
   }

   /**
    * 更新桌面小控件
    */
   private fun updateWidget(action: String) {
      val intent = Intent(action)
      intent.putExtra(ACTION_IS_WIDGET, true)
      intent.putExtra(Extras.PLAY_STATUS, isPlaying())
      if (action == META_CHANGED) {
         intent.putExtra(Extras.SONG, mPlayingMusic)
      }
      sendBroadcast(intent)
   }

   /**
    * 获取标题
    *
    * @return
    */
   fun getTitle(): String? {
      return if (mPlayingMusic != null) {
         mPlayingMusic!!.title
      } else null
   }

   /**
    * 获取歌手专辑
    *
    * @return
    */
   fun getArtistName(): String? {
      return if (mPlayingMusic != null) {
         mPlayingMusic!!.artist
         // return ConvertUtils.getArtistAndAlbum(mPlayingMusic.getArtist(), mPlayingMusic.getAlbum());
      } else null
   }

   /**
    * 获取专辑名
    *
    * @return
    */
   private fun getAlbumName(): String? {
      return if (mPlayingMusic != null) {
         mPlayingMusic!!.artist
      } else null
   }

   /**
    * 获取当前音乐
    *
    * @return
    */
   fun getPlayingMusic(): Music? {
      return if (mPlayingMusic != null) {
         mPlayingMusic
      } else null
   }

   /**
    * 设置播放队列
    *
    * @param playQueue 播放队列
    */
   fun setPlayQueue(playQueue: List<Music>) {
      mPlayQueue!!.clear()
      mHistoryPos.clear()
      mPlayQueue!!.addAll(playQueue)
      savePlayQueue(true)
   }


   /**
    * 获取播放队列
    *
    * @return 获取播放队列
    */
   fun getPlayQueue(): List<Music>? {
      return if (mPlayQueue!!.size > 0) {
         mPlayQueue
      } else mPlayQueue
   }


   /**
    * 获取当前音乐在播放队列中的位置
    *
    * @return 当前音乐在播放队列中的位置
    */
   fun getPlayPosition(): Int {
      return if (mPlayingPos >= 0) {
         mPlayingPos
      } else
         0
   }

   /**
    * 初始化通知栏
    */
   private fun initNotify() {
      mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val albumName = getAlbumName()
      val artistName = getArtistName()
      val text = if (TextUtils.isEmpty(albumName))
         artistName
      else
         "$artistName - $albumName"

      val playButtonResId = if (isMusicPlaying)
         R.drawable.ic_pause
      else
         R.drawable.ic_play

      val nowPlayingIntent = Intent(this, PlayerDetailActivity::class.java)
      nowPlayingIntent.action = Constants.DEAULT_NOTIFICATION
      val clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT)
      if (mNotificationPostTime == 0L) {
         mNotificationPostTime = System.currentTimeMillis()
      }
      mNotificationBuilder = NotificationCompat.Builder(this, initChannelId())
              .setSmallIcon(R.drawable.ic_music)
              .setContentIntent(clickIntent)
              .setContentTitle(getTitle())
              .setContentText(text)
              .setWhen(mNotificationPostTime)
              .addAction(playButtonResId, "",
                      retrievePlaybackAction(ACTION_PLAY_PAUSE))
              .addAction(R.drawable.ic_skip_previous,
                      "",
                      retrievePlaybackAction(ACTION_PREV))
              .addAction(R.drawable.ic_skip_next,
                      "",
                      retrievePlaybackAction(ACTION_NEXT))
              .addAction(R.drawable.ic_lyric,
                      "",
                      retrievePlaybackAction(ACTION_LYRIC))
              .addAction(R.drawable.ic_clear,
                      "",
                      retrievePlaybackAction(ACTION_CLOSE))
              .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                      this, PlaybackStateCompat.ACTION_STOP))


      if (SystemUtils.isJellyBeanMR1()) {
         mNotificationBuilder!!.setShowWhen(false)
      }
      if (SystemUtils.isLollipop()) {
         //线控
         isRunningForeground = true
         mNotificationBuilder!!.setVisibility(Notification.VISIBILITY_PUBLIC)
         val style = android.support.v4.media.app.NotificationCompat.MediaStyle()
                 .setMediaSession(mediaSessionManager!!.getMediaSession())
                 .setShowActionsInCompactView(1, 0, 2, 3, 4)
         mNotificationBuilder!!.setStyle(style)
      }

      if (mPlayingMusic != null) {
         CoverLoader.loadImageViewByMusic(this, mPlayingMusic, object : CoverLoader.BitmapCallBack {
            override fun showBitmap(bitmap: Bitmap?) {
               mNotificationBuilder!!.setLargeIcon(bitmap)
               mNotification = mNotificationBuilder!!.build()
               mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
            }
         })
      }
      mNotification = mNotificationBuilder!!.build()
   }


   /**
    * 创建Notification ChannelID
    *
    * @return 频道id
    */
   private fun initChannelId(): String {
      // 通知渠道的id
      val id = "music_lake_01"
      // 用户可以看到的通知渠道的名字.
      val name = "CocoMusic"
      // 用户可以看到的通知渠道的描述
      val description = "通知栏播放控制"
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
         val importance = NotificationManager.IMPORTANCE_LOW
         val mChannel: NotificationChannel
         mChannel = NotificationChannel(id, name, importance)
         mChannel.description = description
         mChannel.enableLights(false)
         mChannel.enableVibration(false)
         //最后在notificationmanager中创建该通知渠道
         mNotificationManager!!.createNotificationChannel(mChannel)
      }
      return id
   }

   private fun retrievePlaybackAction(action: String): PendingIntent {
      val intent = Intent(action)
      intent.component = ComponentName(this, MusicPlayerService::class.java)
      return PendingIntent.getService(this, 0, intent, 0)
   }

   fun getAudioId(): String? {
      return if (mPlayingMusic != null) {
         mPlayingMusic!!.mid
      } else {
         null
      }
   }

   private var lyricTimer: Timer? = null

   fun showDesktopLyric(show: Boolean) {
      if (show) {
         // 开启定时器，每隔0.5秒刷新一次
         if (lyricTimer == null) {
            lyricTimer = Timer()
            lyricTimer!!.scheduleAtFixedRate(object : TimerTask() {
               override fun run() {
//                        mFloatLyricViewManager!!.updateLyric(getCurrentPosition(), getDuration())
               }
            }, 0, 1)
         }
      } else {
         if (lyricTimer != null) {
            lyricTimer!!.cancel()
            lyricTimer = null
         }
//            mFloatLyricViewManager!!.removeFloatLyricView(this)
      }
   }

   /**
    * 电话监听
    */
   private inner class ServicePhoneStateListener : PhoneStateListener() {
      override fun onCallStateChanged(state: Int, incomingNumber: String) {
         // TODO Auto-generated method stub
         when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK   //通话状态
               , TelephonyManager.CALL_STATE_RINGING   //通话状态
            -> pause()
         }
      }
   }

   /**
    * 更新状态栏通知
    */
   private fun updateNotification(changePlayStatus: Boolean) {
      if (!changePlayStatus) {
         if (mPlayingMusic != null) {
            CoverLoader.loadImageViewByMusic(this, mPlayingMusic, object : CoverLoader.BitmapCallBack {
               override fun showBitmap(bitmap: Bitmap?) {
                  mNotificationBuilder?.setLargeIcon(bitmap)
                  mNotification = mNotificationBuilder?.build()
                  mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
               }
            })
         }
         mNotificationBuilder!!.setContentTitle(getTitle())
         mNotificationBuilder!!.setContentText(getArtistName())
         mNotificationBuilder!!.setTicker(getTitle() + "-" + getArtistName())
         updateNotificationStatus()
      } else {
         updateNotificationStatus()
      }
      mNotification = mNotificationBuilder!!.build()
//        mFloatLyricViewManager!!.updatePlayStatus(isMusicPlaying)
      startForeground(NOTIFICATION_ID, mNotification)
      mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
   }

   private fun updateNotificationStatus() {
      if (isPlaying())
         mNotificationBuilder!!.mActions.get(0).icon = R.drawable.ic_pause
      else
         mNotificationBuilder!!.mActions.get(0).icon = R.drawable.ic_play
   }

   /**
    * 取消通知
    */
   private fun cancelNotification() {
      stopForeground(true)
      mNotificationManager!!.cancel(NOTIFICATION_ID)
      isRunningForeground = false
   }

   /**
    * Service broadcastReceiver 监听service中广播
    */
   inner class ServiceReceiver : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
         LogUtils.d("$TAG intent.action")
         if (!intent.getBooleanExtra(ACTION_IS_WIDGET, false)) {
            handleCommandIntent(intent)
         }
      }
   }


   /**
    * Intent处理
    *
    * @param intent
    */
   private fun handleCommandIntent(intent: Intent) {
      val action = intent.action
      val command = if (SERVICE_CMD == action) intent.getStringExtra(CMD_NAME) else null
      if (DEBUG)
         LogUtils.d("$TAG handleCommandIntent: action = $action, command = $command")

      if (CMD_NEXT == command || ACTION_NEXT == action) {
         next(false)
      } else if (CMD_PREVIOUS == command || ACTION_PREV == action) {
         prev()
      } else if (CMD_TOGGLE_PAUSE == command || PLAY_STATE_CHANGED == action
              || ACTION_PLAY_PAUSE == action) {
         if (isPlaying()) {
            pause()
            mPausedByTransientLossOfFocus = false
         } else {
            play()
         }
      } else if (UNLOCK_DESKTOP_LYRIC == command) {
//            mFloatLyricViewManager!!.saveLock(false, true)
      } else if (CMD_PAUSE == command) {
         pause()
         mPausedByTransientLossOfFocus = false
      } else if (CMD_PLAY == command) {
         play()
      } else if (CMD_STOP == command) {
         pause()
         mPausedByTransientLossOfFocus = false
         seekTo(0, false)
         releaseServiceUiAndStop()
      } else if (ACTION_LYRIC == action) {
         startFloatLyric()
      } else if (ACTION_CLOSE == action) {
         stop(true)
         stopSelf()
         releaseServiceUiAndStop()
         System.exit(0)
      }
   }

   /**
    * 开启歌词
    */
   private fun startFloatLyric() {
//        if (SystemUtils.isOpenFloatWindow()) {
//            showLyric = !showLyric
//            showDesktopLyric(showLyric)
//        } else {
//            SystemUtils.applySystemWindow()
//        }
   }

   /**
    * 耳机插入广播接收器
    */
   inner class HeadsetPlugInReceiver : BroadcastReceiver() {
      init {
         if (Build.VERSION.SDK_INT >= 21) {
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG)
         } else {
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG)
         }
      }

      override fun onReceive(context: Context, intent: Intent?) {
         if (intent != null && intent.hasExtra("state")) {
            //通过判断 "state" 来知道状态
            val isPlugIn = intent.extras!!.getInt("state") == 1
            LogUtils.e("$TAG 耳机插入状态 ：$isPlugIn")
         }
      }
   }


   /**
    * 耳机拔出广播接收器
    */
   inner class HeadsetReceiver : BroadcastReceiver() {

      internal val bluetoothAdapter: BluetoothAdapter?

      init {
         intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY) //有线耳机拔出变化
         intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) //蓝牙耳机连接变化

         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
      }

      override fun onReceive(context: Context, intent: Intent) {
         if (isRunningForeground) {
            //当前是正在运行的时候才能通过媒体按键来操作音频
            when (intent.action) {
               BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                  LogUtils.e("蓝牙耳机插拔状态改变")
                  if (bluetoothAdapter != null &&
                          BluetoothProfile.STATE_DISCONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) &&
                          isPlaying()) {
                     //蓝牙耳机断开连接 同时当前音乐正在播放 则将其暂停
                     pause()
                  }
               }
               AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                  LogUtils.e("有线耳机插拔状态改变")
                  if (isPlaying()) {
                     //有线耳机断开连接 同时当前音乐正在播放 则将其暂停
                     pause()
                  }
               }
            }
         }
      }
   }

   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   override fun onDestroy() {
      super.onDestroy()
      disposable?.dispose()
      // Remove any sound effects
      val audioEffectsIntent = Intent(
              AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
      audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
      audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
      sendBroadcast(audioEffectsIntent)
      savePlayQueue(false)

      //释放mPlayer
      if (mPlayer != null) {
         mPlayer!!.stop()
         isMusicPlaying = false
         mPlayer!!.release()
         mPlayer = null
      }

      // 释放Handler资源
      if (mHandler != null) {
         mHandler!!.removeCallbacksAndMessages(null)
         mHandler = null
      }

      // 释放工作线程资源
      if (mWorkThread != null && mWorkThread!!.isAlive) {
         mWorkThread!!.quitSafely()
         mWorkThread!!.interrupt()
         mWorkThread = null
      }

      audioAndFocusManager!!.abandonAudioFocus()
      cancelNotification()

      //注销广播
      unregisterReceiver(mServiceReceiver)
      unregisterReceiver(mHeadsetReceiver)
      unregisterReceiver(mHeadsetPlugInReceiver)
      unregisterReceiver(mStandardWidget)

      if (mWakeLock.isHeld)
         mWakeLock.release()
      // 释放 MediaPlayer 时有错误，服务端始终没有彻底关闭，【退出】应用后再次打开应用，启动服务时，
      // 调用 MediaPlayer 的 reset 方法抛出异常，java.lang.illageStatExeception
      Process.killProcess(Process.myPid())
   }

}

