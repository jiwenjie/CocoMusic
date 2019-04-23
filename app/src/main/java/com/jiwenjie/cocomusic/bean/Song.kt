package com.jiwenjie.cocomusic.bean

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:
 *  version:1.0
 */
class Song() : Serializable, Parcelable {

   //与客户端 DBSongInfo 中的 data 域对应，对于同一首歌曲（文件路径相同），两者应该相同
   var path: String? = null

   constructor(parcel: Parcel) : this() {
      path = parcel.readString()
   }

   fun Song(path: String) {
      this.path = path
   }

   override fun hashCode(): Int {
      return path.hashCode()
   }

   override fun writeToParcel(dest: Parcel, flags: Int) {
      dest.writeString(this.path)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<Song> {
      override fun createFromParcel(parcel: Parcel): Song {
         return Song(parcel)
      }

      override fun newArray(size: Int): Array<Song?> {
         return arrayOfNulls(size)
      }
   }
}