package com.jiwenjie.cocomusic.playservice

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
import com.jiwenjie.cocomusic.ui.PlayerActivity
import com.jiwenjie.cocomusic.utils.CoverLoader
import com.jiwenjie.cocomusic.utils.FileUtils
import com.jiwenjie.cocomusic.utils.SPUtils
import com.jiwenjie.cocomusic.widget.StandardWidget
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:播放service
 *  version:1.0
 */
class MusicPlayerService : Service() {

    /**
     * 错误次数，超过最大错误次数，自动停止播放
     */
    private var playErrorTimes = 0
    private var MAX_ERROR_TIMES = 5

    private var mPlayer: MusicPlayerEngine? = null
    var mWakeLock: PowerManager.WakeLock? = null
    private var powerManager: PowerManager? = null

    var mPlayingMusic: Music? = null
    private var mPlayQueue = ArrayList<Music>()
    private val mHistoryPos = ArrayList<Int>()

    private var mPlayingPos = -1
    private val mNextPlayPos = -1
    private var mPlaylistId = Constants.PLAYLIST_QUEUE_ID

    // 广播接收者
    internal var mServiceReceiver: ServiceReceiver? = null
    internal var mHeadsetReceiver: HeadsetReceiver? = null
    internal var mStandardWidget: StandardWidget? = null
    internal var mHeadsetPlugInReceiver: HeadsetPlugInReceiver? = null
    internal var intentFilter: IntentFilter? = null

    //    private var mFloatLyricViewManager: FloatLyricViewManager? = null     // todo 歌词有关的
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
    private var mServiceInUse = false

    // 工作线程和 Handler
    private var mHandler: MusicPlayerHandler? = null
    private var mWorkThreak: HandlerThread? = null
    // 主线程 Handler
    private var mMainHandler: Handler? = null

    private var showLyric = false   // todo 默认值

    companion object {

        /********************** 初始化变量部分 **********************/

        private val TAG = MusicPlayerService::class.java.simpleName

        val ACTION_SERVICE = "com.jiwenjie.cocomusic.service"     // 广播标志
        // 通知栏
        val ACTION_PREV = "com.jiwenjie.cocomusic.notify.prev"      // 上一首广播标志
        val ACTION_NEXT = "com.jiwenjie.cocomusic.notify.next"      // 下一首广播标志
        val ACTION_PLAY_PAUSE = "com.jiwenjie.cocomusic.notify.play_state"      // 播放暂停广播
        val ACTION_CLOSE = "com.jiwenjie.cocomusic.notify.close"      // 播放暂停广播
        val ACTION_IS_WIDGET = "STORM_ACTION_IS_WIDGET"      // 播放暂停广播

        val ACTION_LYRIC = "com.jiwenjie.cocomusic.notify.lyric"
        val PLAY_STATE_CHANGED = "com.jiwenjie.cocomusic.play_state"    // 播放暂停广播

        val PLAY_STATE_LOADING_CHANGED = "com.jiwenjie.cocomusic.play_state_loading"    // 播放 loading
        val DURATION_CHANGED = "com.jiwenjie.cocomusic.duration"    // 播放时长

        val TRACK_ERROR = "com.jiwenjie.cocomusic.error"
        val SHUTDOWN = "com.jiwenjie.cocomusic.shutdown"
        val REFRESH = "com.jiwenjie.cocomusic.refresh"

        val PLAY_QUEUE_CLEAR = "com.jiwenjie.cocomusic.play_queue_clear"    // 清空播放队列
        val PLAY_QUEUE_CHANGE = "com.jiwenjie.cocomusic.play_queue_change"  // 播放队列改变

        val META_CHANGED = "com.jiwenjie.cocomusic.metachanged"     // 状态改变（歌曲替换）
        val SCHEDULE_CHANGED = "com.jiwenjie.cocomusic.schedule"    // 定时广播

        val CMD_TOGGLE_PAUSE = "toggle_pause"   // 按键播放暂停
        val CMD_NEXT = "next"   // 按键下一首
        val CMD_PREVIOUS = "previous"   // 按键上一首
        val CMD_PAUSE = "pause" // 按键暂停
        val CMD_PLAY = "play"   // 按键播放
        val CMD_STOP = "stop"   // 按键停止
        val CMD_FORWARD = "forward" // 按键停止(单词意思：向前)
        val CMD_REWIND = "reward"   // (单词意思：奖励)
        val SERVICE_CMD = "cmd_service" // 状态改变
        val FROM_MEDIA_BUTTON = "media" // 状态改变
        val CMD_NAME = "name"   // 状态改变
        val UNLOCK_DESKTOP_LYRIC = "unlock_lyric"   // 音量改变增加

        val TRACK_WENT_TO_NEXT = 2  // 下一首
        val RELEASE_WAKELOCK = 3    // 播放完成
        val TRACK_PLAY_ENDED = 4    // 播放完成
        val TRACK_PLAY_ERROR = 5    // 播放出错

        val PREPARE_ASYNC_UPDATE = 7    // PrepareAsync 装载进程
        val PLAYER_PREPARED = 8     // mediaplayer 准备完成

        val AUDIO_FOCUS_CHANGE = 12     // 音频焦点改变
        val VOLUME_FADE_DOWN = 13       // 音量改变减少
        val VOLUME_FADE_UP = 14     // 音量改变增加

        private var NOTIFICATION_ID = 0x123
        private var mNotificationPostTime = 0
        private var mServiceStartId = -1

        private var DEBUG = true

        var totalTime = 0

        /*********************************************************************************/

        /********************** 获取对象实例部分 **********************/

        private var instance: MusicPlayerService? = null

        fun getInstance(): MusicPlayerService? {
            return instance
        }

        /*************************************************************************************/

        /********************** 静态方法部分 start **********************/


        private val listenerList = ArrayList<PlayProgressListener>()

        fun addProgressListener(listener: PlayProgressListener) {
            listenerList.add(listener)
        }

        fun removeProgressListener(listener: PlayProgressListener) {
            listenerList.remove(listener)
        }

        /*********************************************************************/
    }

    inner class MusicPlayerHandler(service: MusicPlayerService, looper: Looper) : Handler(looper) {
        private var mService: WeakReference<MusicPlayerService>? = null
        private var mCurrentVolume = 1.0f

        init {
            mService = WeakReference(service)
        }

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val service = mService!!.get()
            synchronized(mService!!) {
                when (msg!!.what) {
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
                    TRACK_WENT_TO_NEXT -> {
                        // mPlayer 播放完毕切换到下一首
                        // service.setAndRecordPlayPos(service.mNextPlayPos);
                        mMainHandler!!.post { service!!.next(true) }
                        // service.updateCursor(service.mPlayQueue.get(service.mPlayPos).mId);
                        // service.bumpSongCount(); //更新歌曲的播放次数
                    }
                    TRACK_PLAY_ENDED -> {
                        // mPlayer 播放完毕且暂时没有下一首
                        if (PlayQueueManager.getPlayModeId() == PlayQueueManager.PLAY_MODE_REPEAT) {
                            service!!.seekTo(0, false)
                            mMainHandler!!.post { service!!.pause() }
                        } else {
                            mMainHandler!!.post { service!!.next(true) }
                        }
                    }
                    TRACK_PLAY_ERROR -> {
                        // mPlayer 播放错误
                        playErrorTimes++
                        if (playErrorTimes < MAX_ERROR_TIMES) {
                            mMainHandler!!.post { service!!.next(true) }
                        } else {
                            mMainHandler!!.post { service!!.pause() }
                        }
                    }
                    RELEASE_WAKELOCK -> {
                        // 释放电源
                        service!!.mWakeLock!!.release()
                    }
                    PREPARE_ASYNC_UPDATE -> {
                        val percent = msg.obj as Int
                        LogUtils.e("$TAG Loading ... $percent")
                        notifyChange(PLAY_STATE_LOADING_CHANGED)
                    }
                    PLAYER_PREPARED -> {
                        // 执行 prepared 之后，准备完成，更新总时长
                        notifyChange(PLAY_STATE_CHANGED)
                    }
                    AUDIO_FOCUS_CHANGE -> {
                        when (msg.arg1) {
                            // 失去音频焦点  暂时失去焦点
                            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                if (service!!.isPlaying()) {
                                    mPausedByTransientLossOfFocus = msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                                }
                                mHandler!!.post { service.pause() }
                            }
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                                removeMessages(VOLUME_FADE_UP)
                                sendEmptyMessage(VOLUME_FADE_DOWN)
                            }
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                // 重新获取焦点且符合音乐的播放条件，开始播放
                                if (!service!!.isPlaying() &&
                                        mPausedByTransientLossOfFocus) {
                                    mPausedByTransientLossOfFocus = false
                                    mCurrentVolume = 0f
                                    service.mPlayer!!.setVolume(mCurrentVolume)
                                    mHandler!!.post { service.play() }
                                } else {
                                    removeMessages(VOLUME_FADE_DOWN)
                                    sendEmptyMessage(VOLUME_FADE_UP)
                                }
                            }
                            else -> {
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private val disposable =
        Observable.interval(500, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listenerList.forEach { it ->
                    it.onProgressUpdate(getCurrentPosition(), getDuration())
                }
            }


    override fun onCreate() {
        super.onCreate()
        instance = this
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
     * 参数配置，AudioManager，锁屏
     */
    @SuppressLint("InvalidWakeLockTag")
    private fun initConfig() {
        // 初始化主线程 Handler
        mMainHandler = Handler(Looper.getMainLooper())
        PlayQueueManager.getPlayModeId()

        // 初始化工作线程
        mWorkThreak = HandlerThread("MusicCocoThread")
        mWorkThreak!!.start()

        mHandler = MusicPlayerHandler(this, mWorkThreak!!.looper)

        // 电源键
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager!!.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PlayerWakelockTag")

//        mFloatLyricViewManager = FloatLyricViewManager(this)

        // 初始化和设置 MediaSessionCompat
        mediaSessionManager = MediaSessionManager(this, mBindStub, mMainHandler!!)
        audioAndFocusManager = AudioAndFocusManager(this, mHandler!!)
    }

    /**
     * 初始化电话监听服务
     */
    private fun initTelephony() {
        val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager // 获取电话通讯服务
        telephonyManager.listen(ServicePhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE)  // 创建一个监听对象，监听电话状态改变事件
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
     * 重新加载当前进度
     */
    fun reloadPlayQueue() {
        mPlayQueue.clear()
        mHistoryPos.clear()
//        mPlayQueue = PlayQueueLoader.INSTANCE.getPlayQueue()  // 获取播放队列
        mPlayingPos = SPUtils.playPosition
        if (mPlayingPos >= 0 && mPlayingPos < mPlayQueue.size) {
            mPlayingMusic = mPlayQueue[mPlayingPos]
            updateNotification(false)
            seekTo(SPUtils.position, true)
            notifyChange(META_CHANGED)
        }
        notifyChange(PLAY_QUEUE_CHANGE)
    }

    /**
     * 初始化广播
     */
    private fun initReceiver() {
        // 实例化过滤器，设置广播
        intentFilter = IntentFilter(ACTION_SERVICE)
        mServiceReceiver = ServiceReceiver()
        mHeadsetReceiver = HeadsetReceiver()
        mStandardWidget = StandardWidget()
        mHeadsetPlugInReceiver = HeadsetPlugInReceiver()
        intentFilter!!.addAction(ACTION_NEXT)
        intentFilter!!.addAction(ACTION_PREV)
        intentFilter!!.addAction(META_CHANGED)
        intentFilter!!.addAction(SHUTDOWN)
        intentFilter!!.addAction(ACTION_PLAY_PAUSE)
        // 注册广播
        registerReceiver(mServiceReceiver, intentFilter)
        registerReceiver(mHeadsetReceiver, intentFilter)
        registerReceiver(mHeadsetPlugInReceiver, intentFilter)
        registerReceiver(mStandardWidget, intentFilter)
    }

    /**
     * 启动Service服务，执行onStartCommand
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mServiceStartId = startId
        mServiceInUse = true
        if (intent != null) {
            val action = intent.action
            if (SHUTDOWN == action) {
                releaseServiceUiAndStop()
                return START_NOT_STICKY
            }
            handleCommandIntent(intent)
        }
        return START_NOT_STICKY
    }

    /**
     * 绑定 Service
     */
    override fun onBind(intent: Intent?): IBinder? {
       return mBindStub
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mServiceInUse = false
        savePlayQueue(false)

        releaseServiceUiAndStop()
        stopSelf(mServiceStartId)
        return true
    }


    private fun saveHistory() {
//        PlayHistoryLoader.INSTANCE.addSongToHistory(mPlayingMusic)
        savePlayQueue(false)
    }

    private fun setAndRecordPlayPos(mNextPlayPos: Int) {
        this.mPlayingPos = mNextPlayPos
    }

    /**
     * 下一首播放
     * music 设置的歌曲
     */
    fun nextPlay(music: Music) {
        if (mPlayQueue.size == 0) {
            play(music)
        } else if (mPlayingPos < mPlayQueue.size) {
            mPlayQueue.add(mPlayingPos + 1, music)
        }
    }

    /**
     * 跳到输入的进度
     */
    fun seekTo(pos: Long, isInit: Boolean) {
        if (mPlayer != null && mPlayer!!.isInitialized() && mPlayingMusic != null) {
            mPlayer!!.seek(pos)
        } else if (isInit) {
            //            playCurrentAndNext();
            //            mPlayer.seek(pos);
            //            mPlayer.pause();
            LogUtils.e("$TAG seekTo 失败")
        }
    }

    /**
     * 根据位置播放音乐
     */
    fun playMusic(position: Int) {
        mPlayingPos = if (position >= mPlayQueue.size || position == -1) {
            PlayQueueManager.getNextPostion(true, mPlayQueue.size, position)
        } else {
            position
        }
        if (mPlayingPos == -1)
            return
        playCurrentAndNext()
    }

    /**
     * 获取正在播放的歌曲
     */
    fun removeFromQueue(position: Int) {
        try {
            if (position == mPlayingPos) {
                mPlayQueue.removeAt(position)
                if (mPlayQueue.size == 0) {
                    clearQueue()
                } else {
                    playMusic(position)
                }
            } else if (position > mPlayingPos) {
                mPlayQueue.removeAt(position)
            } else if (position < mPlayingPos) {
                mPlayQueue.removeAt(position)
                mPlayingPos -= 1
            }
            notifyChange(PLAY_QUEUE_CLEAR)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取正在播放的歌曲[本地][网络]
     */
    fun clearQueue() {
        mPlayingMusic = null
        isMusicPlaying = false
        mPlayingPos = -1
        mPlayQueue.clear()
        mHistoryPos.clear()
        savePlayQueue(true)
        stop(true)
        notifyChange(META_CHANGED)
        notifyChange(PLAY_STATE_CHANGED)
        notifyChange(PLAY_QUEUE_CLEAR)
    }

    /**
     * 设置播放队列
     *
     * @param playQueue 播放队列
     */
    fun setPlayQueue(playQueue: List<Music>) {
        mPlayQueue.clear()
        mHistoryPos.clear()
        mPlayQueue.addAll(playQueue)
        savePlayQueue(true)
    }

    /**
     * 获取播放队列
     *
     * @return 获取播放队列
     */
    fun getPlayQueue(): List<Music> {
        return if (mPlayQueue.size > 0) {
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
    @SuppressLint("ObsoleteSdkInt")
    private fun initNotify() {
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val albumName = getAlbumName()
        val artistName = getArtistName()

        val text = if (TextUtils.isEmpty(albumName)) artistName
                                else "$artistName - $albumName"

        val playButtonResId = if (isMusicPlaying) R.drawable.ic_pause
                                else R.drawable.ic_play

        val nowPlayingIntent = Intent(this, PlayerActivity::class.java)
        nowPlayingIntent.action = Constants.DEAULT_NOTIFICATION
        val clickIntent = PendingIntent.getActivity(this, 0, nowPlayingIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis().toInt()
        }
        mNotificationBuilder = NotificationCompat.Builder(this, initChannelId())
            .setSmallIcon(R.drawable.ic_music)
            .setContentIntent(clickIntent)
            .setContentTitle(getTitle())
            .setContentText(text)
            .setWhen(mNotificationPostTime.toLong())
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
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this, PlaybackStateCompat.ACTION_STOP))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mNotificationBuilder!!.setShowWhen(false)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
     * 创建 Notification ChannelID
     * 频道 id
     */
    private fun initChannelId(): String {
        // 通知渠道的 id
        val id = "music_coco_01"
        // 用户可以看到通知渠道的名字
        val name = "CocoMusic"
        // 用户可以看到的通知渠道的描述
        val description = "通知栏播放控制"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val important = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, important)
            mChannel.description = description
            mChannel.enableLights(false)
            mChannel.enableVibration(false)
            // 最后在 notificationmanager 中创建该通知渠道
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
            //            return ConvertUtils.getArtistAndAlbum(mPlayingMusic.getArtist(), mPlayingMusic.getAlbum());
        } else null
    }

    /**
     * 获取专辑名
     *
     * @return
     */
    private fun getAlbumName(): String? {
        return if (mPlayingMusic != null) {
            mPlayingMusic!!.album
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
     * 下一首
     */
    fun next(isAuto: Boolean?) {
        synchronized(this) {
            mPlayingPos = PlayQueueManager.getNextPostion(isAuto, mPlayQueue.size, mPlayingPos)
            LogUtils.e("$TAG next: $mPlayingPos")
            stop(false)
            playCurrentAndNext()
        }
    }

    /**
     * 获取下一首位置
     */
    private fun getNextPosition(isAuto: Boolean) : Int {
        val playModeId = PlayQueueManager.getPlayModeId()
        if (mPlayQueue == null || mPlayQueue.isEmpty()) {
            return -1
        }
        if (mPlayQueue.size == 1) {
            return 0
        }
        if (playModeId == PlayQueueManager.PLAY_MODE_REPEAT && isAuto) {
            if (mPlayingPos < 0) {
                return 0
            } else {
                return mPlayingPos
            }
        } else if (playModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
            return java.util.Random().nextInt(mPlayQueue.size)
        } else {
            if (mPlayingPos == mPlayQueue.size - 1) {
                return 0
            } else if (mPlayingPos < mPlayQueue.size - 1) {
                return mPlayingPos + 1
            }
        }
        return mPlayingPos
    }

    /**
     * 上一首
     */
    fun prev() {
        synchronized(this) {
            mPlayingPos = PlayQueueManager.getPreviousPosition(mPlayQueue.size, mPlayingPos)
            stop(false)
            playCurrentAndNext()
        }
    }

    /**
     * 获取上一首位置
     */
    fun getPreviousPosition(): Int {
        val playModeId = PlayQueueManager.getPlayModeId()
        if (mPlayQueue == null || mPlayQueue.isEmpty()) {
            return -1
        }
        if (mPlayQueue.size == 1) {
            return 0
        }
        if (playModeId == PlayQueueManager.PLAY_MODE_REPEAT) {
            if (mPlayingPos < 0) {
                return 0
            }
        } else if (playModeId == PlayQueueManager.PLAY_MODE_RANDOM) {
            mPlayingPos = java.util.Random().nextInt(mPlayQueue.size)
            return java.util.Random().nextInt(mPlayQueue.size)
        } else {
            if (mPlayingPos == 0) {
                return mPlayQueue.size - 1
            } else if (mPlayingPos > 0) {
                return mPlayingPos - 1
            }
        }
        return mPlayingPos
    }

    /**
     * 播放当前歌曲
     */
    private fun playCurrentAndNext() {
        synchronized(this) {
            if (mPlayingPos >= mPlayQueue.size || mPlayingPos < 0) return

            mPlayingMusic = mPlayQueue[mPlayingPos]
            notifyChange(META_CHANGED)
            if (mPlayingMusic!!.uri.isNullOrEmpty() || !Objects.equals(mPlayingMusic!!.type, Constants.LOCAL) ||
                mPlayingMusic!!.uri!! == "" || mPlayingMusic!!.uri!! == "null"
            ) {
                // todo 判断如果不是本地歌曲的话则调用网络请求播放网络歌曲
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
                mHandler!!.sendEmptyMessage(VOLUME_FADE_UP)     // 组件调到正常音量
                isMusicPlaying = true
            }
        }
    }

    private inner class ServicePhoneStateListener : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_OFFHOOK,
                TelephonyManager.CALL_STATE_RINGING -> {    //通话状态
                    pause()
                }
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
                        mNotificationBuilder!!.setLargeIcon(bitmap)
                        mNotification = mNotificationBuilder!!.build()
                        mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
                    }
                })
                mNotificationBuilder!!.setContentTitle(getTitle())
                mNotificationBuilder!!.setContentText(getArtistName())
                mNotificationBuilder!!.setTicker(getTitle() + " - " + getArtistName())
                updateNotificationStatus()
            } else {
                updateNotificationStatus()
            }
            mNotification = mNotificationBuilder!!.build()
//            mFloatLyricViewManager.updatePlayStatus(isMusicPlaying)
            startForeground(NOTIFICATION_ID, mNotification)
            mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
        }
    }

    private fun updateNotificationStatus() {
        if (isPlaying()) {
            mNotificationBuilder!!.mActions[0].icon = R.drawable.ic_pause
        } else {
            mNotificationBuilder!!.mActions[0].icon = R.drawable.ic_play
        }
    }

    /**
     * 停止播放
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
     * 音乐播放
     */
    fun play() {
        if (mPlayer!!.isInitialized()) {
            mPlayer!!.start()
            isMusicPlaying = true
            notifyChange(PLAY_STATE_CHANGED)
            audioAndFocusManager!!.requestAudioFocus()
            mHandler!!.removeMessages(VOLUME_FADE_DOWN)
            mHandler!!.sendEmptyMessage(VOLUME_FADE_UP)     // 组件调到正常音量

            updateNotification(true)
        } else {
            playCurrentAndNext()
        }
    }

    /**
     * [在线音乐] 加入播放队列并播放音乐
     */
    fun play(music: Music?) {
        if (music == null) return
        if (mPlayingPos == -1 || mPlayQueue.size == 0) {
            mPlayQueue.add(music)
            mPlayingPos = 0
        } else if (mPlayingPos < mPlayQueue.size) {
            mPlayQueue.add(mPlayingPos, music)
        } else {
            mPlayQueue.add(mPlayQueue.size, music)
        }
        mPlayingMusic = music
        playCurrentAndNext()
    }

    /**
     * 切换歌单播放
     * 1. 歌单不一样切换
     */
    fun play(musicList: ArrayList<Music>, id: Int, pid: String) {
        if (musicList.size <= id) return
        if (mPlaylistId != pid || mPlayQueue.size == 0 || mPlayQueue.size != musicList.size) {
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
                            AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION
                        )
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
     * 异常播放，自动切换到下一首
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
     * 开启歌词
     */
    fun startFloatLyric() {
        // todo 暂时不写
//        if (SystemUtils.isOpenFloatWindow()) {
//            showLyric = !showLyric
//            showDesktopLyric(showLyric)
//        } else {
//            SystemUtils.applySystemWindow()
//        }
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
        if (mPlayer != null && mPlayer!!.isInitialized() && mPlayer!!.isPrepared()) {
            return mPlayer!!.getDuration()
        }
        return 0
    }

    /**
     * 是否准备播放
     */
    fun isPrepared(): Boolean {
        if (mPlayer != null) {
            return mPlayer!!.isPrepared()
        }
        return false
    }

    /**
     * 发送更新广播
     *
     * @param what 发送更新广播
     */
    private fun notifyChange(what: String) {
        when (what) {
            META_CHANGED -> {
//                mFloatLyricViewManager.loadLyric(mPlayingMusic)   // 加载歌词方法
                updateWidget(META_CHANGED)
                EventBus.getDefault().post(MetaChangedEvent(mPlayingMusic))
            }
            PLAY_STATE_CHANGED -> {
                updateWidget(ACTION_PLAY_PAUSE)
                mediaSessionManager!!.updatePlaybackState()
                EventBus.getDefault().post(StatusChangedEvent(isPrepared(), isPlaying()))
            }
            PLAY_QUEUE_CLEAR, PLAY_QUEUE_CHANGE -> {
                EventBus.getDefault().post(PlaylistEvent(Constants.PLAYLIST_QQ_ID, null))
            }
        }
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

    private var lyricTimer: Timer? = null

    fun showDesktopLyric(show: Boolean) {
        if (show) {
            // 开启定时器，每隔 0.5s 刷新一次
            if (lyricTimer == null) {
                lyricTimer = Timer()
                lyricTimer!!.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
//                        mFloatLyricViewManager.updateLyric(getCurrentPosition(), getDuration())
                    }
                }, 0, 1)
            }
        } else {
            if (lyricTimer != null) {
                lyricTimer!!.cancel()
                lyricTimer = null
            }
//            mFloatLyricViewManager.removeFloatLyricView(this)
        }
    }


    /**
     * 保存播放队列
     */
    private fun savePlayQueue(full: Boolean) {
        // todo 暂不写有关队列的相关操作
//        if (full) {
//            PlayQueueLoader.INSTANCE.updateQueue(mPlayQueue)
//        }
//        if (mPlayingMusic != null) {
//            //保存歌曲id
//            SPUtils.saveCurrentSongId(mPlayingMusic.getMid())
//        }
//        //保存歌曲id
//        SPUtils.setPlayPosition(mPlayingPos)
//        //保存歌曲进度
//        SPUtils.savePosition(getCurrentPosition())
//
//        LogUtil.e(TAG, "save 保存歌曲id=" + mPlayingPos + " 歌曲进度= " + getCurrentPosition())
        notifyChange(PLAY_QUEUE_CHANGE)
    }

    /************************************** 广播相关 start *************************************/
    // 监听 service 中的广播
    inner class ServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LogUtils.d(TAG + intent?.action)
            if (!intent!!.getBooleanExtra(ACTION_IS_WIDGET, false)) {
                handleCommandIntent(intent)
            }
        }
    }

    /**
     * 耳机拔出广播接收器
     */
    inner class HeadsetReceiver : BroadcastReceiver() {

        private val bluetoothAdapter: BluetoothAdapter?

        init {
            intentFilter!!.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY) //有线耳机拔出变化
            intentFilter!!.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED) //蓝牙耳机连接变化

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }

        override fun onReceive(context: Context, intent: Intent) {
            if (isRunningForeground) {
                //当前是正在运行的时候才能通过媒体按键来操作音频
                when (intent.action) {
                    BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                        LogUtils.e("蓝牙耳机插拔状态改变")
                        if (bluetoothAdapter != null &&
                            BluetoothProfile.STATE_DISCONNECTED == bluetoothAdapter.getProfileConnectionState(
                                BluetoothProfile.HEADSET) && isPlaying()) {
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

    /**
     * 耳机插入广播接收器
     */
    inner class HeadsetPlugInReceiver : BroadcastReceiver() {
        init {
            if (Build.VERSION.SDK_INT >= 21) {
                intentFilter!!.addAction(AudioManager.ACTION_HEADSET_PLUG)
            } else {
                intentFilter!!.addAction(Intent.ACTION_HEADSET_PLUG)
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
    /**************************************  广播相关 end  *************************************/

    /**
     * Intent 处理
     */
    private fun handleCommandIntent(intent: Intent?) {
        val action = intent!!.action
        val command = if (SERVICE_CMD == action) intent.getStringExtra(CMD_NAME) else null

        if (DEBUG) {
            LogUtils.d("$TAG handleCommandIntent: action = $action, command = $command")
        }

        when (action) {
            CMD_NEXT, ACTION_NEXT -> {
                next(false)
            }
            CMD_PREVIOUS, ACTION_PREV -> {
                prev()
            }
            CMD_TOGGLE_PAUSE, PLAY_STATE_CHANGED, ACTION_PLAY_PAUSE -> {
                if (isPlaying()) {
                    pause()
                    mPausedByTransientLossOfFocus = false
                } else {
                    play()
                }
            }
            UNLOCK_DESKTOP_LYRIC -> {
//                mFloatLyricViewManager.saveLock(false, true)
            }
            CMD_PAUSE -> {
                pause()
                mPausedByTransientLossOfFocus = false
            }
            CMD_PLAY -> {
                play()
            }
            CMD_STOP -> {
                pause()
                mPausedByTransientLossOfFocus = false
                seekTo(0, false)
                releaseServiceUiAndStop()
            }
            ACTION_LYRIC -> {
                startFloatLyric()
            }
            ACTION_CLOSE -> {
                stop(true)
                stopSelf()
                releaseServiceUiAndStop()
                System.exit(0)
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

    /**************************** 在广播中处理的方法 *****************************/

    /**
     * 释放通知栏
     */
    private fun releaseServiceUiAndStop() {
        if (isPlaying() || mHandler!!.hasMessages(TRACK_PLAY_ENDED)) return

        cancelNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager!!.release()
        }

        if (!mServiceInUse) {
            savePlayQueue(false)
            stopSelf(mServiceStartId)
        }
    }

    /**
     * 取消通知
     */
    private fun cancelNotification() {
        stopForeground(true)
        mNotificationManager!!.cancel(NOTIFICATION_ID)
        isRunningForeground = false
    }

    fun getAudioSessionId(): Int {
        synchronized(this) {
            return mPlayer!!.getAudioSessionId()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        // remove any sound effects
        val audioEffectsIntent = Intent(AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION)
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId())
        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName)
        sendBroadcast(audioEffectsIntent)
        savePlayQueue(false)

        // 释放 mPlayer
        if (mPlayer != null) {
            mPlayer!!.stop()
            isMusicPlaying = false
            mPlayer!!.release()
            mPlayer = null
        }

        // 释放 Handler 资源
        if (mHandler != null) {
            mHandler!!.removeCallbacksAndMessages(null)
            mHandler = null
        }

        // 释放工作线程资源
        if (mWorkThreak != null && mWorkThreak!!.isAlive) {
            mWorkThreak!!.quitSafely()
            mWorkThreak!!.interrupt()
            mWorkThreak = null
        }

        audioAndFocusManager!!.abandonAudioFocus()
        cancelNotification()

        // 注销广播
        unregisterReceiver(mServiceReceiver)
        unregisterReceiver(mHeadsetReceiver)
        unregisterReceiver(mHeadsetPlugInReceiver)
        unregisterReceiver(mStandardWidget)

        if (mWakeLock!!.isHeld) {
            mWakeLock!!.release()
        }
    }
}

