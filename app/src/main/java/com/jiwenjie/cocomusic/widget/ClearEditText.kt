package com.jiwenjie.cocomusic.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.jiwenjie.cocomusic.R

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/30
 *  desc:实现带删除功能的编辑框，同时重写了部分方法，可以监听用户点击向下按钮
 *  version:1.0
 */
class ClearEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs), View.OnFocusChangeListener, TextWatcher {

   private var mClearDrawable: Drawable? = null

   init {
      init(context, attrs)
   }

   private fun init(context: Context, attrs: AttributeSet?) {
      mClearDrawable = compoundDrawables[2]
      if (mClearDrawable == null) {
         mClearDrawable = ContextCompat.getDrawable(context, R.drawable.ic_clear)
         (mClearDrawable as Drawable).setTint(ContextCompat.getColor(context, R.color.translucent_grey))
      }
      mClearDrawable?.setBounds(0, 0, mClearDrawable!!.intrinsicWidth, mClearDrawable!!.intrinsicHeight)
      setClearIconVisible(false)    // 默认不显示清除图标
      onFocusChangeListener = this
      addTextChangedListener(this)
   }

   /**
    * 我们不能直接给 EditText 设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
    * 当我们按下的位置在 EditText 的宽度 - 图标到控件右边的间距 - 图标的宽度  和
    * EditText 的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
    */
   @SuppressLint("ClickableViewAccessibility")
   override fun onTouchEvent(event: MotionEvent?): Boolean {
      if (compoundDrawables[2] != null) {
         if (event!!.action == MotionEvent.ACTION_UP) {
            val touchable = event.x > (width - paddingRight - mClearDrawable!!.intrinsicWidth) &&
                    (event.x < (width - paddingRight))
            if (touchable) {
               this.setText("")
            }
         }
      }
      return super.onTouchEvent(event)
   }

   /**
    * 设置清楚图标的显示与隐藏，调用 setCompoundDrawables 给 EditText 绘制上去
    */
   protected fun setClearIconVisible(visible: Boolean) {
      val rightDrawable = if (visible) {
         mClearDrawable
      } else {
         null
      }

      setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], rightDrawable, compoundDrawables[3])
   }

   /**
    * 当 ClearEditText 焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
    */
   override fun onFocusChange(v: View?, hasFocus: Boolean) {
      if (hasFocus) {
         setClearIconVisible(text!!.isNotEmpty())
      } else {
         setClearIconVisible(false)
      }
   }

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

   /**
    * 当输入框内容发生变化的时候回调
    */
   override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
      setClearIconVisible(text!!.isNotEmpty())
   }

   override fun afterTextChanged(s: Editable?) {

   }

   override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

   }

}











