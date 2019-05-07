package com.jiwenjie.cocomusic.utils

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import com.jiwenjie.cocomusic.CocoApp

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/06
 *  desc:
 *  version:1.0
 */
object SystemUtils {

   //判断是否是android 6.0
   fun isJellyBeanMR1(): Boolean {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
   }

   //判断是否是android 8.0
   fun isO(): Boolean {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
   }


   //判断是否是android 6.0
   fun isMarshmallow(): Boolean {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
   }

   //判断是否是android 5.0
   fun isLollipop(): Boolean {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
   }

   //判断是否是android 4.0
   fun isKITKAT(): Boolean {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
   }


   /**
    * 判断是否打开“悬浮窗权限”
    *
    * @return
    */
   fun isOpenFloatWindow(): Boolean? {
      return null
//      return FloatUtil.INSTANCE.checkPermission(MusicApp.getAppContext())
   }

   /**
    * 检查申请打开“悬浮窗权限”
    *
    * @return
    */
   fun applySystemWindow() {
//      FloatUtil.INSTANCE.applyOrShowFloatWindow(MusicApp.getAppContext())
   }


   /**
    * 判断是否打开“有权查看使用权限的应用”这个选项
    *
    * @return
    */
   fun isOpenUsageAccess(): Boolean {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isNoOptions()) {
         isNoSwitch()
      } else {
         true
      }
   }


   /**
    * 判断当前设备中有没有“有权查看使用权限的应用”这个选项
    *
    * @return
    */
   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   private fun isNoOptions(): Boolean {
      val packageManager = CocoApp.contextInstance.packageManager
      val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
      val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
      return list.size > 0
   }


   /**
    * 判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
    *
    * @return
    */
   @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   private fun isNoSwitch(): Boolean {
      val dujinyang = System.currentTimeMillis()
      val usageStatsManager = CocoApp.contextInstance.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
      var queryUsageStats: List<UsageStats>? = null
      if (usageStatsManager != null) {
         queryUsageStats = usageStatsManager.queryUsageStats(
                 UsageStatsManager.INTERVAL_BEST, 0, dujinyang)
      }
      return !(queryUsageStats == null || queryUsageStats.isEmpty())
   }
}