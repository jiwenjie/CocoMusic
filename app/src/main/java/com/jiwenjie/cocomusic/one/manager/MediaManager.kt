package com.jiwenjie.cocomusic.one.manager

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.jiwenjie.cocomusic.aidl.Song
import com.jiwenjie.cocomusic.bean.SongInfo
import com.jiwenjie.cocomusic.utils.StringUtils

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/24
 *  desc:线程安全的单例。该类在播放进程中也会用到，此时单例失效
 *  version:1.0
 */
class MediaManager private constructor() {

   private var songs: HashSet<SongInfo>? = null

   companion object {
      @Volatile
      private var MEDIAMANAGER: MediaManager? = null

      // 传入 Application Context
      fun getInstance(): MediaManager {
         if (MEDIAMANAGER == null) {
            synchronized(MediaManager::class.java) {
               if (MEDIAMANAGER == null)
                  MEDIAMANAGER =
                          MediaManager()
            }
         }
         return MEDIAMANAGER!!
      }
   }

   fun refreshData(context: Context): HashSet<SongInfo>? {
      if (songs == null) {
         songs = HashSet()
      } else {
         songs!!.clear()
      }

      val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
              null, null, null) ?: return songs

      while (cursor.moveToNext()) {
         val song = SongInfo()
         song.album_id = cursor.getString(cursor.getColumnIndex(SongInfo.ALBUM_ID))
         song.album_path = getAlbumArtPicPath(context, song.album_id)
         song.title_key = cursor.getString(cursor.getColumnIndex(SongInfo.TITLE_KEY))
         song.artist_key = cursor.getString(cursor.getColumnIndex(SongInfo.ARTIST_KEY))
         song.album_key = cursor.getString(cursor.getColumnIndex(SongInfo.ALBUM_KEY))
         song.artist = cursor.getString(cursor.getColumnIndex(SongInfo.ARTIST))
         song.album = cursor.getString(cursor.getColumnIndex(SongInfo.ALBUM))
         song.data = cursor.getString(cursor.getColumnIndex(SongInfo.DATA))
         song.display_name = cursor.getString(cursor.getColumnIndex(SongInfo.DISPLAY_NAME))
         song.title = cursor.getString(cursor.getColumnIndex(SongInfo.TITLE))
         song.mime_type = cursor.getString(cursor.getColumnIndex(SongInfo.MIME_TYPE))
         song.year = cursor.getLong(cursor.getColumnIndex(SongInfo.YEAR))
         song.duration = cursor.getLong(cursor.getColumnIndex(SongInfo.DURATION))
         song.size = cursor.getLong(cursor.getColumnIndex(SongInfo.SIZE))
         song.date_added = cursor.getLong(cursor.getColumnIndex(SongInfo.DATE_ADDED))
         song.date_modified = cursor.getLong(cursor.getColumnIndex(SongInfo.DATE_MODIFIED))

         songs!!.add(song)
      }
      cursor.close()
      return songs
   }

   // 根据专辑 id 获取专辑图片的保存路径
   @SuppressLint("Recycle")
   @Synchronized
   private fun getAlbumArtPicPath(context: Context, albumId: String): String? {

      // 小米应用商店检测crash ，错误信息：[31188,0,com.duan.musicoco,13155908,java.lang.IllegalStateException,
      // Unknown URL: content://media/external/audio/albums/null,Parcel.java,1548]
      if (!StringUtils.isReal(albumId)) {
         return null
      }

      val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
      var imagePath: String? = null
      val uri = Uri.parse("content://media" + MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.path + "/" + albumId)

      val cur = context.contentResolver.query(uri, projection, null, null, null) ?: return null

      if (cur.count > 0 && cur.columnCount > 0) {
         cur.moveToNext()
         imagePath = cur.getString(0)
      }
      cur.close()

      return imagePath
   }

   fun getSongInfo(context: Context, song: Song): SongInfo? {
      if (songs == null)
         refreshData(context)
      var info: SongInfo? = null
      for (s in this.songs!!) {
         info = s
         if (info.data.equals(song.path)) {
            break
         }
      }
      return info
   }

   fun getSongInfo(context: Context, path: String): SongInfo? {
      return getSongInfo(context, Song(path))
   }


   fun scanSdCard(context: Context, listener: MediaScannerConnection.OnScanCompletedListener) {
      MediaScannerConnection.scanFile(context, arrayOf(Environment.getExternalStorageDirectory().absolutePath), null, listener)
   }

   /**
    * 检查媒体库是否为空，是否要重新获取数据之后在确定，这个过程可能比较耗时
    * 为空则返回 true
    */
   fun emptyMediaLibrary(context: Context, refresh: Boolean): Boolean {
      if (refresh) {
         refreshData(context)
      } else {
         if (songs == null)
            refreshData(context)
      }
      return songs!!.size == 0
   }
}




