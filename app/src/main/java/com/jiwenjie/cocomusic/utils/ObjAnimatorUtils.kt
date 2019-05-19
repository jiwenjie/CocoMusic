package com.jiwenjie.cocomusic.utils

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.support.v4.content.ContextCompat
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/19
 *  desc:动画的工具类
 *  version:1.0
 */
object ObjAnimatorUtils {

   // 缩放的动画方法
   fun startAnim(target: Any, onePropertyName: String,
                         twoPropertyName: String, isSelect: Boolean,
                         originValue: Float = 16f, selectValue: Float = 19f) {
      val objAnim: ObjectAnimator
      val colorAnim: ObjectAnimator
      if (isSelect) {   // 被选中的动画
         objAnim = ObjectAnimator.ofFloat(target, onePropertyName, originValue, selectValue)      // 缩放
         colorAnim = ObjectAnimator.ofObject(target, twoPropertyName, ArgbEvaluator(),
                 ContextCompat.getColor(CocoApp.contextInstance, R.color.alpha_60_white), ContextCompat.getColor(CocoApp.contextInstance, R.color.white))     // 字体颜色
      } else {    // 未被选中的动画
         objAnim = ObjectAnimator.ofFloat(target, onePropertyName, selectValue, originValue)      // 缩放
         colorAnim = ObjectAnimator.ofObject(target, twoPropertyName, ArgbEvaluator(),
                 ContextCompat.getColor(CocoApp.contextInstance, R.color.white), ContextCompat.getColor(CocoApp.contextInstance, R.color.alpha_60_white))     // 字体颜色
      }

      val animSet = AnimatorSet()
      animSet.playTogether(objAnim, colorAnim)
      animSet.duration = 300
      animSet.start()
   }
}