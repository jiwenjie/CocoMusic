@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.jiwenjie.cocomusic.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.WindowManager
import org.jetbrains.anko.support.v4.dip

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/09
 *  desc:
 *  version:1.0
 */
class PlayQueueDialog : BottomSheetDialogFragment() {

   private var mBehavior: BottomSheetBehavior<*>? = null

   companion object {
      fun newInstance(): PlayQueueDialog {
         val args = Bundle()
         val fragment = PlayQueueDialog()
         fragment.arguments = args
         return fragment
      }
   }

   override fun onStart() {
      super.onStart()
      val dialog = dialog
      dialog.setCanceledOnTouchOutside(true)
      val window = dialog.window

      val params = window?.attributes
      params?.gravity = Gravity.BOTTOM
      params?.width = WindowManager.LayoutParams.MATCH_PARENT
//      params?.height = CocoApp.screenSize.y / 7 * 4
      window.attributes = params
      window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      mBehavior?.peekHeight = params?.height ?: dip(200)
      //默认全屏展开
      mBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
   }

   fun showSongs() {

   }

   fun showIt(context: AppCompatActivity) {
      val fm = context.supportFragmentManager
      show(fm, "dialog")
   }
}