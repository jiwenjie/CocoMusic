package com.jiwenjie.cocomusic.ui.contract

import android.app.Activity
import com.jiwenjie.basepart.mvp.BaseMvpViewImpl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/19
 *  desc:mainActivity 的契约类
 *  version:1.0
 */
interface MainContract {

   interface View : BaseMvpViewImpl {

   }

   interface Presenter {
      // can add change avatar and username and so on, these is not implement now

      fun setTimeStopPlay(activity: Activity)   // stop play when you set time
      fun openScan(activity: Activity)

      fun showMyFriend(activity: Activity)      // search all friend

      fun changeAppTheme(activity: Activity)
      fun openMusicAlarmClock(activity: Activity)

      fun openMusicCloud(activity: Activity)

      fun setNightType(activity: Activity)      // 设置夜间模式
      fun openSetting(activity: Activity)       // 打开设置界面
      fun exitApplication()   // 退出应用
   }
}