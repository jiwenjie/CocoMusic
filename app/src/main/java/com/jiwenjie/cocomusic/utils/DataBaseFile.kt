package com.jiwenjie.cocomusic.utils

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jiwenjie.cocomusic.aidl.Music
import android.arch.persistence.room.Room
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.common.DBConstant
import com.jiwenjie.cocomusic.common.DBContract

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/04
 *  desc:Room 数据库工具类
 *  version:1.0
 */
@Database(
    entities = [Music::class, Artist::class],
//    entities = [Music::class],
    version = DBConstant.DB_VERSION)

abstract class CocoDataBase : RoomDatabase() {
    abstract fun musicDao(): DBContract.MusicDao
    abstract fun artistDao(): DBContract.ArtistDao
}

object CocoDataBaseUtil {
    private val instance : CocoDataBase by lazy {
        Room.databaseBuilder(CocoApp.contextInstance, CocoDataBase::class.java, DBConstant.DB_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    LogUtils.e("DataBase created")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    LogUtils.e("DataBase opened")
                }
            })
            .allowMainThreadQueries()   // 允许主线程查询，不过一般不推荐
            .fallbackToDestructiveMigration()   // 失败重新创建
            .build()
    }

    val mMusicDao = instance.musicDao()
    val mArtistDao = instance.artistDao()
}












