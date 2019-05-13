package com.jiwenjie.cocomusic

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.basepart.utils.AssetsLoader
import com.jiwenjie.cocomusic.ui.activity.MainActivity
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
class SplashActivity : BaseActivity() {

    @SuppressLint("CheckResult")
    override fun initActivity(savedInstanceState: Bundle?) {
        fullScreen()   // 设置全屏
        activity_splash_slognText.typeface =
                AssetsLoader.getFontSourceFromAssets(CocoApp.contextInstance, "font/fanxinshu.TTF")
        // 设置动画
        val objAnimX = ObjectAnimator.ofFloat(bottomLyt, "scaleX", 0.4f, 1f)
        val objAnimY = ObjectAnimator.ofFloat(bottomLyt, "scaleY", 0.4f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objAnimX, objAnimY)
        animatorSet.duration = 300
        animatorSet.startDelay = 1500
        animatorSet.start()

        Observable.timer(3000, TimeUnit.MILLISECONDS)
            .compose(RxJavaUtils.applyObservableAsync())
            .subscribe {
                MainActivity.runActivity(this)
                finish()
            }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true
    }

    override fun getLayoutId(): Int = R.layout.activity_splash
}