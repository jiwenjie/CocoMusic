package com.jiwenjie.cocomusic.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.Album
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.common.Constants
import android.content.ContentResolver



/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/06
 *  desc:有关音乐的方法工具类
 *  version:1.0
 */
object SongLoader {
    /**
     * 获取所有艺术家
     */
    fun getAllArtists(beanList: ArrayList<Music>): MutableList<Artist>? {
//        var result = CocoDataBaseUtil.mArtistDao.getAllArtist()
//        if (result.isNullOrEmpty() || result?.size == 0) {
//            result = mutableListOf()
//            val cursor = CocoDataBaseUtil.mArtistDao.getArtistByMusic()
//            if (cursor != null && cursor.count > 0) {
//                while (cursor.moveToNext()) {
//                    val artist = MusicCursorWrapper(cursor).artists
//                    CocoDataBaseUtil.mArtistDao.saveArtist(artist)
////                    artist.saveOrUpdate("artistId = ?", artist.artistId.toString())
//                    result.add(artist)
//                }
//            }
//        }
//        return result

        val resultList = mutableListOf<Artist>()

        val jsobObj = mutableMapOf<String, Int>() //可增删改查的mutable map，初始化为空。
        if (beanList.size > 0) {
            for (music in beanList) {

            }
        }

        return resultList
    }

    /**
     * Android 扫描获取到的数据
     */
    private fun getSongsForMedia(context: Context, cursor: Cursor?): MutableList<Music>? {
            val results = mutableListOf<Music>()
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    Log.e("SongLoader", "------")
                    do {
                        val artistId = cursor.getString(9)
                        val id = cursor.getLong(0)
                        val title = cursor.getString(1)
                        val artist = cursor.getString(2)
                        val album = cursor.getString(3)
                        val duration = cursor.getInt(4)
                        val trackNumber = cursor.getInt(5)
                        val albumId = cursor.getString(7)
                        val path = cursor.getString(8)
                        val coverUri = CoverLoader.getCoverUri(context, albumId)
                        val music = Music()
                        music.type = Constants.LOCAL
                        music.isOnline = false
                        music.mid = id.toString()
                        music.album = album
                        music.albumId = albumId
                        music.artist = if (artist == "<unknown>") "未知歌手" else artist
                        music.artistId = artistId
                        music.uri = path
                        coverUri?.let { music.coverUri = it }
                        music.trackNumber = trackNumber
                        music.duration = duration.toLong()
                        music.title = title
                        music.date = System.currentTimeMillis()
                        CocoDataBaseUtil.mMusicDao.saveOrUpdate(music)  // 插入数据库
                        results.add(music)
                    } while (cursor.moveToNext())
                }
                cursor?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        return results
    }

    fun getAllLocalSongs(context: Context): MutableList<Music>? {
        return getSongsForMedia(context, makeSongCursor(context, null, null))
    }

    fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        val songSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder)
    }

    private fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?, sortOrder: String?): Cursor? {
        val inContentUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        val externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        return context.contentResolver.query(
            externalContentUri,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA, "is_music"),
            null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
    }
}























