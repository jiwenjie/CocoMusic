package com.jiwenjie.cocomusic.utils

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.common.Constants
import android.arch.persistence.room.Room
import android.content.Context
import android.provider.SyncStateContract


/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/04
 *  desc:Room 数据库工具类
 *  version:1.0
 */
@Database(entities = [Music::class, Artist::class], version = SyncStateContract.Constants.DATABASE_VERSION, exportSchema = false)
abstract class DataBaseUtil : RoomDatabase() {

    private var INSTANCE: DataBaseUtil? = null

//    abstract fun userDao(): UserDao

    fun getInstance(context: Context): DataBaseUtil? {
        if (INSTANCE == null) {
        synchronized(DataBaseUtil::class.java) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, DataBaseUtil::class.java, "user.db")
                    .build()
            }

            }
        }
        return INSTANCE
    }

    fun onDestroy() {
        INSTANCE = null
    }
}












