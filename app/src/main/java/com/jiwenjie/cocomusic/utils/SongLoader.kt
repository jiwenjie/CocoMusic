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
    fun getAllArtists(): MutableList<Artist>? {
        var result = CocoDataBaseUtil.mArtistDao.getAllArtist()
        if (result.isNullOrEmpty() || result?.size == 0) {
            result = mutableListOf()
            val cursor = CocoDataBaseUtil.mArtistDao.getArtistByMusic()
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val artist = MusicCursorWrapper(cursor).artists
                    CocoDataBaseUtil.mArtistDao.saveArtist(artist)
//                    artist.saveOrUpdate("artistId = ?", artist.artistId.toString())
                    result.add(artist)
                }
            }
        }
        return result
    }

    /**
     * 获取所有专辑
     *
     * @param context
     * @return
     */
    fun getAllAlbums(): MutableList<Album>? {
//        val result = DaoLitepal.getAllAlbum()
//        if (result.size == 0) {
//            return DaoLitepal.updateAlbumList()
//        }
        return null
    }

    /**
     * 获取艺术家所有歌曲
     */
    fun getSongsForArtist(artistName: String?): MutableList<Music>? {
        return CocoDataBaseUtil.mMusicDao.getArtistMusic(artistName)
    }

    /**
     * 获取专辑所有歌曲
     *
     * @param context
     * @return
     */
    fun getSongsForAlbum(albumName: String?): MutableList<Music>? {
//        return LitePal.where("isonline =0 and album like ?", "%$albumName%").find(Music::class.java)
        return null
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
                        val is_music = cursor.getInt(9)
                        val id = cursor.getLong(0)
                        val title = cursor.getString(1)
                        val artist = cursor.getString(2)
                        val album = cursor.getString(3)
                        val duration = cursor.getInt(4)
                        val trackNumber = cursor.getInt(5)
                        val artistId = cursor.getString(6)
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
        val results = CocoDataBaseUtil.mMusicDao.getAllLocalMusic()
        if (!results.isNullOrEmpty()) return results
        return getSongsForMedia(context, makeSongCursor(context, null, null))
    }

    fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        val songSortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        return makeSongCursor(context, selection, paramArrayOfString, songSortOrder)
    }

    fun getSongListInFolder(context: Context, path: String): MutableList<Music>? {
        val whereArgs = arrayOf("$path%")
        return getSongsForMedia(context, makeSongCursor(context, MediaStore.Audio.Media.DATA + " LIKE ?", whereArgs, null))
    }

    private fun makeSongCursor(context: Context, selection: String?, paramArrayOfString: Array<String>?, sortOrder: String?): Cursor? {
        var selectionStatement = "duration>60000 AND is_music=1 AND title != ''"

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = "$selectionStatement AND $selection"
        }
        return context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA, "is_music"),
            selectionStatement, paramArrayOfString, sortOrder)
    }

    fun removeMusicList(musicList: MutableList<Music>) {
        musicList.forEach {
//            removeSong(it)
        }
    }
}























