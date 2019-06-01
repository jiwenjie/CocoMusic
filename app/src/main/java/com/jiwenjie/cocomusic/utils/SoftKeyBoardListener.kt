package com.jiwenjie.cocomusic.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/01
 *  desc:监听输入法的显示或者隐藏并获取输入法的高度
 *  version:1.0
 */
class SoftKeyBoardListener(activity: Activity) {

   private val rootView: View//activity的根视图
   internal var rootViewVisibleHeight: Int =
           SharedPreferenceUtils.getIntMethod(SharedPreferenceUtils.KEY_ORIGINAL_VISIBLE_HEIGHT, 0)   //记录根视图的显示高度
   private var onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener? = null

   init {
      //获取activity的根视图
      rootView = activity.window.decorView

      /**
       * actually the keyword is a dialog, so when the keyword pop, the layout will be changed,
       * and I add listener on layout in onResume is perfect. other method is not perfect
       */
      rootView.viewTreeObserver.addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
         //获取当前根视图在屏幕上显示的大小
         val r = Rect()
         rootView.getWindowVisibleDisplayFrame(r)
         val visibleHeight = r.height()
         if (rootViewVisibleHeight == 0) {
            rootViewVisibleHeight = visibleHeight
            return@OnGlobalLayoutListener
         }

         //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
         if (rootViewVisibleHeight == visibleHeight) {
            return@OnGlobalLayoutListener
         }

         //根视图显示高度变小超过200，可以看作软键盘显示了
         if (rootViewVisibleHeight - visibleHeight > 200) {
            if (onSoftKeyBoardChangeListener != null) {
               onSoftKeyBoardChangeListener!!.keyBoardShow(rootViewVisibleHeight - visibleHeight)
            }
            rootViewVisibleHeight = visibleHeight
            return@OnGlobalLayoutListener
         }

         //根视图显示高度变大超过200，可以看作软键盘隐藏了
         if (visibleHeight - rootViewVisibleHeight > 200) {
            if (onSoftKeyBoardChangeListener != null) {
               onSoftKeyBoardChangeListener!!.keyBoardHide(visibleHeight - rootViewVisibleHeight)
            }
            rootViewVisibleHeight = visibleHeight
            return@OnGlobalLayoutListener
         }
      })
   }

   private fun setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
      this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener
   }

   abstract class OnSoftKeyBoardChangeListener {
      open fun keyBoardShow(height: Int) {
      }

      open fun keyBoardHide(height: Int) {
      }
   }

   companion object {
      fun setOnKeyboardChangeListener(activity: Activity, onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
         val softKeyBoardListener = SoftKeyBoardListener(activity)
         softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener)
      }
   }
}
