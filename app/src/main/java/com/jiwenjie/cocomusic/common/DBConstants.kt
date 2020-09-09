package com.jiwenjie.cocomusic.common

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.database.Cursor
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.Artist

/**
 *  author:stormwenjie
 *  email:Jiwenjie97@gmail.com
 *  time:2019/06/08
 *  desc:
 */
object DBConstant {
    const val DB_VERSION = 3
    const val DB_NAME = "CocoRoom"
}

interface DBContract {

    @Dao    // 音乐 Dao
    interface MusicDao {
        @Query("select * from music")
        fun getAllLocalMusic(): MutableList<Music>?

        /**
         * 获取艺术家的苏哦有歌曲
         */
        @Query("select * from music where isonline = 0 and artist like :artistName")
        fun getArtistMusic(artistName: String?): MutableList<Music>?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveOrUpdate(vararg music: Music): List<Long>
    }

    @Dao    // 艺术家 dao
    interface ArtistDao {
        @Query("select * from artist")
        fun getAllArtist(): MutableList<Artist>?

        @Query("SELECT music.artistid, music.artist, count(music.title) as num FROM music where music.isonline = 0 and music.type=\"local\" GROUP BY music.artist")
        fun getArtistByMusic(): Cursor?

        /**
         * after search success for music then insert into artist
         */
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun saveArtist(vararg artist: Artist): List<Long>
    }
}