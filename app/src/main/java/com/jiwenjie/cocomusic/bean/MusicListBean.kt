package com.jiwenjie.cocomusic.bean

import android.os.Parcel
import android.os.Parcelable

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/20
 *  desc:该 bean 自己先实现，暂时没有接口可以得到
 *  version:1.0
 */
class MusicListBean() : Parcelable {

   var name: String? = null         // 歌单的名称
   var createtime: String? = null   // 歌单创建时间
   var ownername: String? = null    // 歌单的拥有者
   var albumUrl: String? = null     // 歌单的封面
   var totalSize = 0                // 歌曲的数量
   var downloadSize = 0             // 已经下载的数量

   constructor(parcel: Parcel) : this() {
      name = parcel.readString()
      createtime = parcel.readString()
      ownername = parcel.readString()
      albumUrl = parcel.readString()
      totalSize = parcel.readInt()
      downloadSize = parcel.readInt()
   }

   override fun writeToParcel(parcel: Parcel?, flags: Int) {
      parcel?.writeString(name)
      parcel?.writeString(createtime)
      parcel?.writeString(ownername)
      parcel?.writeString(albumUrl)
      parcel?.writeInt(totalSize)
      parcel?.writeInt(downloadSize)
   }

   override fun describeContents(): Int {
      return 0
   }

   companion object CREATOR : Parcelable.Creator<MusicListBean> {
      override fun createFromParcel(parcel: Parcel): MusicListBean {
         return MusicListBean(parcel)
      }

      override fun newArray(size: Int): Array<MusicListBean?> {
         return arrayOfNulls(size)
      }
   }
}