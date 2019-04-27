package com.jiwenjie.cocomusic

import android.view.View
import com.jiwenjie.cocomusic.aidl.IPlayControl
import com.jiwenjie.cocomusic.interfaces.ContentUpdatable
import com.jiwenjie.cocomusic.interfaces.OnUpdateStatusChanged
import com.jiwenjie.cocomusic.interfaces.ThemeChangeable
import com.jiwenjie.cocomusic.interfaces.ThemeEnum
import com.jiwenjie.cocomusic.manager.BroadcastManager
import com.jiwenjie.cocomusic.manager.MediaManager
import com.jiwenjie.cocomusic.service.PlayServiceCallback

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:
 *  version:1.0
 */
class BottomNavigationController : View.OnClickListener,
    PlayServiceCallback,
    ContentUpdatable,
    ThemeChangeable {

    private var broadcastManager: BroadcastManager? = null
    private var mControl: IPlayControl? = null

    private var mediaManager: MediaManager? = null

//    private var playNotifyManager: PlayN

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun songChanged() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(obj: Any, statusChanged: OnUpdateStatusChanged) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun noData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun themeChange(themeEnum: ThemeEnum, colors: IntArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}