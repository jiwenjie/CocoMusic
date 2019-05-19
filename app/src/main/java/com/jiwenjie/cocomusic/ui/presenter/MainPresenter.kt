package com.jiwenjie.cocomusic.ui.presenter

import android.app.Activity
import android.os.Process
import com.jiwenjie.basepart.mvp.BaseMvpPresenter
import com.jiwenjie.cocomusic.ui.contract.MainContract
import java.lang.System.exit

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/19
 *  desc:
 *  version:1.0
 */
class MainPresenter(view: MainContract.View) : BaseMvpPresenter<MainContract.View>(view), MainContract.Presenter {

    override fun setTimeStopPlay(activity: Activity) {

    }

    override fun openScan(activity: Activity) {

    }

    override fun showMyFriend(activity: Activity) {

    }

    override fun changeAppTheme(activity: Activity) {

    }

    override fun openMusicAlarmClock(activity: Activity) {

    }

    override fun openMusicCloud(activity: Activity) {

    }

    override fun setNightType(activity: Activity) {

    }

    override fun openSetting(activity: Activity) {

    }

    override fun exitApplication() {
        exit(0)
        Process.killProcess(Process.myPid())
    }
}