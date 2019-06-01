package com.jiwenjie.cocomusic.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import android.view.KeyEvent

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/01
 *  desc:自定义 EditText，可以监听到用户点击向下的按钮
 *  version:1.0
 */
class MyEditText(context: Context, attributeSet: AttributeSet): AppCompatEditText(context, attributeSet) {

   /**
    * 监听输入法的向下按键，监听普通的 onKeyDown 不行，因为事件在中间被拦截了
    */
   override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
      if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == 1 && onKeyBoardHideListener != null) {
         onKeyBoardHideListener!!.onKeyHide()
      }
      return super.onKeyPreIme(keyCode, event)
   }

   /**
    * 键盘监听接口
    */
   private var onKeyBoardHideListener: OnKeyBoardHideListener? = null

   fun setOnKeyBoardHideListener(onKeyBoardHideListener :OnKeyBoardHideListener) {
      this.onKeyBoardHideListener = onKeyBoardHideListener;
   }

   interface OnKeyBoardHideListener {
      fun onKeyHide()
   }
}