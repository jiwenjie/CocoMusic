package com.jiwenjie.cocomusic.event

import com.jiwenjie.cocomusic.bean.Playlist
import com.jiwenjie.cocomusic.common.Constants


/**
 * Author   : D22434
 * version  : 2018/3/1
 * function :
 */
class PlaylistEvent(var type: String? = Constants.PLAYLIST_CUSTOM_ID, val playlist: Playlist? = null)
