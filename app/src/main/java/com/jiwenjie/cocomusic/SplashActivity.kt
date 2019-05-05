package com.jiwenjie.cocomusic

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.basepart.utils.AssetsLoader
import com.jiwenjie.cocomusic.ui.MainActivity
import com.jiwenjie.cocomusic.utils.RxJavaUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/04
 *  desc:
 *  version:1.0
 */
class SplashActivity: BaseActivity() {

   @SuppressLint("CheckResult")
   override fun initActivity(savedInstanceState: Bundle?) {
      fullScreen()   // 设置全屏
      activity_splash_slognText.typeface = AssetsLoader.getFontSourceFromAssets(CocoApp.contextInstance, "font/fanxinshu.TTF")
      // 设置动画
      val objAnimX = ObjectAnimator.ofFloat(activity_splash_iconImg, "scaleX", 0f, 1f)
      val objAnimY = ObjectAnimator.ofFloat(activity_splash_iconImg, "scaleY", 0f, 1f)
      val animatorSet = AnimatorSet()
      animatorSet.playTogether(objAnimX, objAnimY)
      animatorSet.duration = 600
      animatorSet.start()

      Observable.timer(1200, TimeUnit.MILLISECONDS)
         .compose(RxJavaUtils.applyObservableAsync())
         .subscribe {
            MainActivity.runActivity(this)
         }
   }


   override fun getLayoutId(): Int = R.layout.activity_splash
}