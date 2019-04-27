package com.jiwenjie.cocomusic.utils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/24
 *  desc:
 *  version:1.0
 */
class StringUtils {

   companion object {
      fun stringToMd5(key: String): String {
         var cacheKey = ""
         try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            cacheKey = bytesToHexString(mDigest.digest())
         } catch (e: Exception) {
            e.printStackTrace()
         }
         return cacheKey
      }

      private fun bytesToHexString(bytes: ByteArray): String {
         val sb = StringBuilder()
         for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
               sb.append('0')
            }
            sb.append(hex)
         }
         return sb.toString()
      }

      private fun getGenTimeMS(misec: Int): String {
         val min = misec / 1000 / 60
         val sec = (misec / 1000) % 60
         val minStr = if (min < 10) "0$min" else min.toString() + ""
         val secStr = if (sec < 10) "0$sec" else sec.toString() + ""
         return "$minStr:$secStr"
      }

      fun getGenDateYMDHMS(time: Long): String {
         val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         return format.format(Date(time))
      }

      fun getGenDateYMD(time: Long): String {
         val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
         return format.format(Date(time))
      }

      fun isReal(string: String): Boolean {
         return string.isNotEmpty() && "null" != string
      }
   }
}











