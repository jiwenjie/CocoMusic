package com.jiwenjie.cocomusic.ui.activity

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import com.jiwenjie.basepart.mvp.BaseMvpPresenter
import com.jiwenjie.basepart.mvp.BaseMvpViewImpl
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.cocomusic.aidl.IMusicService
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/19
 *  desc:用在音乐播放界面的 MVP 界面
 *  version:1.0
 */
abstract class PlayBaseMvpActivity<V : BaseMvpViewImpl, P : BaseMvpPresenter<V>> :
        BaseActivity(),
        ServiceConnection {

   protected val mHandler by lazy { Handler() }
   protected var mToken: PlayManager.ServiceToken? = null
   var isPause = true

   protected val disposables = ArrayList<Disposable>()

   protected lateinit var mPresenter: P

   /**
    * this.super.onCreate music be used
    */
   override fun onCreate(savedInstanceState: Bundle?) {
      mPresenter = initPresenter()
      EventBus.getDefault().register(this)
      mToken = PlayManager.bindToService(this, this)
      super.onCreate(savedInstanceState)
      /* 注册 lifecycle */
      lifecycle.addObserver(mPresenter)
   }

   abstract fun initPresenter(): P

   override fun onStart() {
      super.onStart()
      isPause = false
   }

   override fun onStop() {
      super.onStop()
      isPause = true
   }

   override fun onDestroy() {
      super.onDestroy()
      EventBus.getDefault().unregister(this)
      if (mToken != null) {
         PlayManager.unbindFromService(mToken)
         mToken = null
      }
      for (disposable in disposables) {
         disposable.dispose()
      }
   }

   override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
      PlayManager.mService = IMusicService.Stub.asInterface(iBinder)
      setListener()
      loadData()
   }

   override fun onServiceDisconnected(componentName: ComponentName) {
      PlayManager.mService = null
   }

   @Subscribe(threadMode = ThreadMode.MAIN)
   fun onDefaultEvent(event: MetaChangedEvent) {
   }
}