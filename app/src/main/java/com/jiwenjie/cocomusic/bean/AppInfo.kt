package com.jiwenjie.cocomusic.bean

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

import java.io.Serializable

class AppInfo : Serializable {

   var applicationInfo: ApplicationInfo? = null
   var versionCode = 0
   /**
    * 图片的icon
    */
   var icon: Drawable? = null

   /**
    * 程序的名字
    */
   var apkName: String? = null

   /**
    * 程序大小
    */
   var apkSize: Long = 0

   /**
    * 表示到底是用户app还是系统app
    * 如果表示为true 就是用户app
    * 如果是false表示系统app
    */
   var isUserApp: Boolean = false

   /**
    * 放置的位置
    */
   var isRom: Boolean = false

   /**
    * 包名
    */
   var apkPackageName: String? = null
}
