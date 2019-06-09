package com.jiwenjie.cocomusic.bean

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/06
 *  desc:
 *  version:1.0
 */
@Entity(tableName = "artist")
class Artist() : Parcelable {

    var name: String? = null

    @PrimaryKey(autoGenerate = true)
    var id: Long? = 0

    @ColumnInfo(name = "artistid")
    var artistId: String?=null
    var count: Int? = 0
    var type: String? = Constants.LOCAL

    @ColumnInfo(name = "picurl")
    var picUrl: String? = null
    var desc: String? = null

    @ColumnInfo(name = "musicsize")
    var musicSize: Int? = 0

    var score: Int? = 0

    @ColumnInfo(name = "albumsize")
    var albumSize: Int? = 0

    @Ignore
    var songs = mutableListOf<Music>()

    @Ignore
    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        id = parcel.readLong()
        artistId = parcel.readString()
        count = parcel.readInt()
        type = parcel.readString()
        picUrl = parcel.readString()
        desc = parcel.readString()
        musicSize = parcel.readInt()
        score = parcel.readInt()
        albumSize = parcel.readInt()
    }

    @Ignore
    constructor(id: Long, name: String, count: Int) : this() {
        this.name = name
        this.artistId = id.toString()
        this.musicSize = count
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        id?.let { parcel.writeLong(it) }
        parcel.writeString(artistId)
        count?.let { parcel.writeInt(it) }
        parcel.writeString(type)
        parcel.writeString(picUrl)
        parcel.writeString(desc)
        musicSize?.let { parcel.writeInt(it) }
        score?.let { parcel.writeInt(it) }
        albumSize?.let { parcel.writeInt(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artist> {
        override fun createFromParcel(parcel: Parcel): Artist {
            return Artist(parcel)
        }

        override fun newArray(size: Int): Array<Artist?> {
            return arrayOfNulls(size)
        }
    }
}