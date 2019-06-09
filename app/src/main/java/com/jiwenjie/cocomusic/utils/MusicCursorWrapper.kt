package com.jiwenjie.cocomusic.utils

import android.database.Cursor
import android.database.CursorWrapper
import com.jiwenjie.cocomusic.bean.Album
import com.jiwenjie.cocomusic.bean.Artist


class MusicCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {

    val album: Album
        get() {
            val id = getString(getColumnIndex("albumid"))
            val name = getString(getColumnIndex("album"))
            val artistId = getLong(getColumnIndex("artistid"))
            val artist = getString(getColumnIndex("artist"))
            val num = getInt(getColumnIndex("num"))
            return Album(id, name, artist, artistId, num)
        }

    val artists: Artist
        get() {
            val id = getLong(getColumnIndex("artistid"))
            val name = getString(getColumnIndex("artist"))
            val num = getInt(getColumnIndex("num"))
            return Artist(id, name, num)
        }

}
