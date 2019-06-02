@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.jiwenjie.cocomusic.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDialogFragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import kotlinx.android.synthetic.main.dialog_fragment_add_musiclist.*
import com.jiwenjie.basepart.utils.ScreenUtils
import com.jiwenjie.cocomusic.utils.CommonUtils
import com.jiwenjie.cocomusic.utils.SoftKeyBoardListener
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/29
 *  desc:添加一个歌单的 Fragment
 *  version:1.0
 */
class AddMusicListDialogFragment : AppCompatDialogFragment() {

   private var mProportion: Double = 0.0    // 默认值

   companion object {
      @JvmStatic
      fun showDialogFragment(fragmentManager: FragmentManager, widthProportion: Double = 0.92): AddMusicListDialogFragment {
         return AddMusicListDialogFragment().apply {
            show(fragmentManager, "AddMusicList")
            setWidthProportion(widthProportion)
         }
      }
   }

   /**
    * set the width about dialogFragment need in onStart, otherwise fail to change width
    */
   override fun onStart() {
      super.onStart()
      if (dialog != null) {
         val dm = DisplayMetrics()
         activity?.windowManager?.defaultDisplay?.getMetrics(dm)
         if (mProportion > 0 && mProportion <= 1)
            dialog.window.setLayout((dm.widthPixels * mProportion).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
      }
   }

   /**
    * Set the proportion of the window occupied(占领) by the dialogFragment width
    */
   private fun setWidthProportion(proportion: Double) {
      this.mProportion = proportion // (比例)
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)     // 去除标题
//      dialog?.window?.setSoftInputMode(
//              WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
//                      WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or
//                      WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

      return inflater.inflate(R.layout.dialog_fragment_add_musiclist, container, false)
   }

   override fun onActivityCreated(savedInstanceState: Bundle?) {
      super.onActivityCreated(savedInstanceState)
      initView()
      initEvent()
   }

   @SuppressLint("CheckResult")
   private fun initView() {
      // 初始化的时候显示
      changeDialogUI()
      // 设置一下延迟，否则会调不出来
      Observable.timer(300, TimeUnit.MILLISECONDS)
              .subscribe {
                 CommonUtils.showSoftInput(activity!!, dialog_musicListNameEdit)   // 自动弹出对话框
              }
   }

   private fun initEvent() {
      dialog_musicListNameEdit.addTextChangedListener(object : SimpleTextWatcher() {
         override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateUi(s)
         }
      })

      dialog_cancel.setOnClickListener {
         this.dismiss()
      }

      dialog_sure.setOnClickListener {
         this.dismiss()
         // 点击保存
         if (selectOnlyme.isChecked) {
            // 被选中说明是隐私歌单
            LogUtils.e("DialogFragment 隐私")
         } else {
            // 默认是公开歌单
            LogUtils.e("DialogFragment 公开")
         }
      }
   }

   /**
    * textChange 更新字数显示
    */
   private fun updateUi(s: CharSequence?) {
      val length = dialog_musicListNameEdit.text.toString().trim { it <= ' ' }.length
      dialog_editTextLength.text = String.format(resources.getString(R.string.music_list_name_length_limit), length)
      if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
         dialog_sure.isEnabled = true
         dialog_sure.alpha = 1f
         dialog_sure.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
      } else {
         dialog_sure.isEnabled = false
         dialog_sure.setTextColor(ContextCompat.getColor(activity!!, R.color.alphaAccent))
         dialog_sure.alpha = 0.5.toFloat()
      }
   }

   /**
    * when the keyboard pop or dismiss make the dialog animation
    */
   fun changeDialogUI() {
      /**
       * 根据键盘的显示隐藏来变化位置
       */
      SoftKeyBoardListener.setOnKeyboardChangeListener(activity!!, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
         override fun keyBoardShow(height: Int) {
            super.keyBoardShow(height)    // 键盘显示
            LogUtils.e("ScreenHeight: $height")
            if (dialog == null) return

            val objAnim = ValueAnimator.ofInt(
                    0, ((ScreenUtils.getScreenHeight(activity!!) - height - dialog.window.attributes.height) / 2) -
                    (ScreenUtils.getScreenHeight(activity!!) / 2))
            objAnim.addUpdateListener {
               windowDeploy(0, it.animatedValue as Int)
            }
            objAnim.duration = 180
            objAnim.start()
         }

         override fun keyBoardHide(height: Int) {
            super.keyBoardHide(height)    // 键盘隐藏
            if (dialog == null) return

            val objAnim = ValueAnimator.ofInt(
                    ((ScreenUtils.getScreenHeight(activity!!) - height - dialog.window.attributes.height) / 2) -
                            (ScreenUtils.getScreenHeight(activity!!) / 2), 0)
            objAnim.addUpdateListener {
               windowDeploy(0, it.animatedValue as Int)
            }
            objAnim.duration = 180
            objAnim.start()
         }
      })
   }

   //设置窗口显示
   fun windowDeploy(x: Int, y: Int) {
//      dialog.window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
//   window.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
      val wl = dialog.window.attributes
      //根据x，y坐标设置窗口需要显示的位置
      wl.x = x //x小于0左移，大于0右移
      wl.y = y //y小于0上移，大于0下移
      // wl.alpha = 0.6f; //设置透明度
      // wl.gravity = Gravity.BOTTOM; //设置重力
      dialog.window.attributes = wl
   }
}