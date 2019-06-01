@file:Suppress("DEPRECATION")

package com.jiwenjie.cocomusic.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.support.v4.content.ContextCompat.getSystemService
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.R.id.textView
import com.jiwenjie.cocomusic.bean.AppInfo
import com.jiwenjie.cocomusic.ui.activity.MainActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.regex.Pattern

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/05
 *  desc:一些公共的工具方法
 *  version:1.0
 */
object CommonUtils {

   /**
    * @param inSampleSize 图片像素的 1/n*n
    */
   fun createBlurredImageFromBitmap(bitmap: Bitmap, inSampleSize: Int): Drawable {
      val rs = RenderScript.create(CocoApp.contextInstance)
      val options = BitmapFactory.Options()
      options.inSampleSize = inSampleSize

      val stream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
      val imageInByte = stream.toByteArray()
      val bis = ByteArrayInputStream(imageInByte)
      val blurTemplate = BitmapFactory.decodeStream(bis, null, options)

      val input = Allocation.createFromBitmap(rs, blurTemplate)
      val output = Allocation.createTyped(rs, input.type)
      val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
      script.setRadius(10f)
      script.setInput(input)
      script.forEach(output)
      output.copyTo(blurTemplate)

      return BitmapDrawable(CocoApp.contextInstance.resources, blurTemplate)
   }

   /**
    * 音乐名格式化
    */
   fun getTitle(title: String?): String {
      var strTitle = stringFilter(title)
      if (strTitle.isNullOrEmpty()) strTitle = CocoApp.contextInstance.getString(R.string.unknown)

      return strTitle!!
   }

   /**
    * 歌手专辑格式化
    */
   fun getArtistAndAlbum(artist: String, album: String): String {
      val strArt = stringFilter(artist)
      val strAlbum = stringFilter(album)

      return if (strArt.isNullOrEmpty() && strAlbum.isNullOrEmpty()) ""
      else if (!strArt.isNullOrEmpty() && strAlbum.isNullOrEmpty()) strArt
      else if (strArt.isNullOrEmpty() && !strAlbum.isNullOrEmpty()) album
      else strArt + strAlbum
   }

   /**
    * 过滤特殊字符
    */
   fun stringFilter(str: String?): String? {
      if (str.isNullOrEmpty()) return null

      val regEx = "<[^>]+>"
      val pattern = Pattern.compile(regEx)
      val matcher = pattern.matcher(str)
      return matcher.replaceAll("").trim()
   }

   /**
    * 获取状态栏高度
    */
   private val statusBarHeight: Int
      get() {
         var result = 0
         val resId = CocoApp.contextInstance.resources.getIdentifier("status_bar_height", "dimen", "android")
         if (resId > 0) {
            result = CocoApp.contextInstance.resources.getDimensionPixelSize(resId)
         }
         return result
      }

   fun transitionStatusBar(activity: Activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         val window = activity.window
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                 WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
         window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                 //该参数指布局能延伸到navigationbar，我们场景中不应加这个参数
                 //                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
         window.statusBarColor = Color.TRANSPARENT
      }
   }

   /**
    * 获取已安装apk的列表
    */
   private fun getAppInfos(): ArrayList<AppInfo> {
      val appInfos = ArrayList<AppInfo>()
      //获取到包的管理者
      val packageManager = CocoApp.contextInstance.packageManager
      //获得所有的安装包
      val installedPackages = packageManager.getInstalledPackages(0)

      //遍历每个安装包，获取对应的信息
      for (packageInfo in installedPackages) {

         val appInfo = AppInfo()

         appInfo.applicationInfo = packageInfo.applicationInfo
         appInfo.versionCode = packageInfo.versionCode

         //得到icon
         val drawable = packageInfo.applicationInfo.loadIcon(packageManager)
         appInfo.icon = drawable

         //得到程序的名字
         val apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
         appInfo.apkName = apkName

         //得到程序的包名
         val packageName = packageInfo.packageName
         appInfo.apkPackageName = packageName

         //得到程序的资源文件夹
         val sourceDir = packageInfo.applicationInfo.sourceDir
         val file = File(sourceDir)
         //得到apk的大小
         val size = file.length()
         appInfo.apkSize = size

         LogUtils.e("---------------------------")
         LogUtils.e("程序的名字:$apkName")
         LogUtils.e("程序的包名:$packageName")
         LogUtils.e("程序的大小:$size")

         //获取到安装应用程序的标记
         val flags = packageInfo.applicationInfo.flags

         appInfo.isUserApp = (flags and ApplicationInfo.FLAG_SYSTEM) == 0

         appInfo.isRom = (flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0

         appInfos.add(appInfo)
      }
      return appInfos
   }


   /**
    * 显示软键盘
    *
    * @param context
    */
   fun showSoftInput(context: Context) {
      val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // 显示软键盘
      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
   }

   /**
    * 显示软键盘
    *
    * @param context
    * @param view 获取焦点的 View
    */
   fun showSoftInput(context: Context, view: View) {
      val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // 显示软键盘
      imm.showSoftInput(view, 0)
   }


   /**
    * 隐藏键盘的方法
    *
    * @param context
    */
   fun hideKeyboard(activity: Activity) {
      val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      // 隐藏软键盘
      imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
   }

   /**
    * 获取软键盘的高度
    */
   fun getKeyboardHeight(activity: Activity) {
      SoftKeyBoardListener.setOnKeyboardChangeListener(activity, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
         override fun keyBoardShow(height: Int) {
            super.keyBoardShow(height)
            LogUtils.e("CommonUtils 键盘显示高度$height")
         }

         /**
          * 因为键盘显示或者隐藏的高度结果都是一样的，所以这里只显示一个。而我们也不用接口来实现，是使用抽象类
          */
//         override fun keyBoardHide(height: Int) {
//           LogUtils.e("CommonUtils 键盘影藏高度$height")
//         }
      })
   }
}
















