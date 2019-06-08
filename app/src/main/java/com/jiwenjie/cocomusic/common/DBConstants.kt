package com.jiwenjie.cocomusic.common

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.Artist

/**
 *  author:stormwenjie
 *  email:Jiwenjie97@gmail.com
 *  time:2019/06/08
 *  desc:
 */
object DBConstant {
    const val DB_VERSION = 1
    const val DB_NAME = "CocoRoom"
}

interface DBContract {

    @Dao    // 音乐 Dao
    interface MusicDao {
        @Query("select * from music")
        fun getAllLocalMusic(): MutableList<Music>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveOrUpdate(vararg music: Music): List<Long>
    }

    @Dao    // 艺术家 dao
    interface ArtistDao {
        @Query("select * from artist")
        fun getAllArtist(): MutableList<Artist>
    }

//    @Dao    // 歌单列表 dao
//    interface MusicListDao {
//
//    }

}