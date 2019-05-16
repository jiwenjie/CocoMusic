package com.jiwenjie.basepart.views

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jiwenjie.basepart.ActivityStackManager
import com.jiwenjie.basepart.PermissionListener
import com.jiwenjie.basepart.utils.LogUtils
import android.view.WindowManager


/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2018/12/14
 *  desc:注意还有权限的动态申请未添加，往后需要添加
 *  version:1.0
 */
@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {

   private var mPermissionListener: PermissionListener? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      // 该行代码可以实现侧滑栏高度充满全屏，不过侧滑栏拉开后状态栏颜色为半透明不是全透明
      fullScreen(getActivity())
      LogUtils.e("onCreate()")
      if (needTransparentStatus()) transparentStatusBar()
      setContentView(getLayoutId())
      ActivityStackManager.addActivity(this)
      initActivity(savedInstanceState)
      loadData()
      setListener()
   }

   override fun onDestroy() {
      super.onDestroy()
      LogUtils.e("onDestroy()")
      ActivityStackManager.removeActivity(this)
   }

   fun getActivity(): Activity {
      return this
   }

   protected abstract fun initActivity(savedInstanceState: Bundle?)

   protected abstract fun getLayoutId(): Int

   // do not implements all times
   protected open fun loadData() {}

   protected open fun needTransparentStatus(): Boolean = false

   protected open fun setListener() {}

   open fun transparentStatusBar() {
      window.decorView.systemUiVisibility =
              View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
              View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) colorSetTargetLollipop() // 判断版本是否为 5.0 之上在调用
      supportActionBar?.hide()
   }

   @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
   open fun fullScreen() {
      window.decorView.systemUiVisibility =
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
              View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
              View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
              View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
              View.SYSTEM_UI_FLAG_FULLSCREEN or
              View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) colorSetTargetLollipop()
      supportActionBar?.hide()
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)  // 这两个属性只有 5.0 之后才有
   fun colorSetTargetLollipop() {
      window.navigationBarColor = Color.TRANSPARENT
      window.statusBarColor = Color.TRANSPARENT
   }

   /* for permissions request */
   open fun onRuntimePermissionsAsk(permissions: kotlin.Array<String>, listener: PermissionListener) {
      this.mPermissionListener = listener
      val activity = ActivityStackManager.getTopActivity()
      val deniedPermissions: MutableList<String> = mutableListOf()

      permissions
              .filterNot { ContextCompat.checkSelfPermission(activity!!, it) == PackageManager.PERMISSION_GRANTED }
              .forEach { deniedPermissions.add(it) }

      if (deniedPermissions.isEmpty())
         mPermissionListener!!.onGranted()
      else
         ActivityCompat.requestPermissions(activity!!, deniedPermissions.toTypedArray(), 1)
   }

   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      if (requestCode == 1) {
         val deniedPermissions: MutableList<String> = mutableListOf()
         if (grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
               if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                  deniedPermissions.add(permissions[i])
            }

            if (deniedPermissions.isEmpty())
               mPermissionListener!!.onGranted()
            else
               mPermissionListener!!.onDenied(deniedPermissions)
         }
      }
   }

   /**
    * 通过设置全屏，设置状态栏透明
    *
    * 改方法可以设置侧滑栏划出后高度全屏，但是当首页有 ViewPager 的时候如果给它设置适配器即失败
    *
    * @param activity
    */
   @SuppressLint("ObsoleteSdkInt")
   open fun fullScreen(activity: Activity) {
      //沉浸式状态栏 android 流海屏的
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
         activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
         val params = activity.window.attributes
         params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
         activity.window.attributes = params
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android 大于5.0的
         //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
         val window = activity.window
         val decorView = window.decorView
         //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
         val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
         decorView.systemUiVisibility = option
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
         window.statusBarColor = Color.TRANSPARENT
         // 导航栏颜色也可以正常设置
         // window.setNavigationBarColor(Color.TRANSPARENT);
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//Android 大于4.4
         val window = activity.window
         val attributes = window.attributes
         val flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
         val flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
         attributes.flags = attributes.flags or flagTranslucentStatus or flagTranslucentNavigation
         // attributes.flags |= flagTranslucentNavigation;
         window.attributes = attributes
      } else {//其余是大于4.0, 4.0.1, 4.0.2
      }
   }
}