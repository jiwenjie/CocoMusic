package com.jiwenjie.cocomusic.ui

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.cocomusic.aidl.IMusicService
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.playservice.PlayManager
import com.jiwenjie.cocomusic.playservice.PlayManager.mService
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:
 *  version:1.0
 */
abstract class PlayBaseActivity : BaseActivity(), ServiceConnection {

    protected val mHandler by lazy { Handler() }
    protected var mToken: PlayManager.ServiceToken? = null
    var isPause = true

    protected val disposables = ArrayList<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        mToken = PlayManager.bindToService(this, this)
    }

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
        mService = IMusicService.Stub.asInterface(iBinder)
        setListener()
        loadData()
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        mService = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDefaultEvent(event: MetaChangedEvent) {
    }
}





















