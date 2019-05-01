package com.jiwenjie.cocomusic.event

import com.jiwenjie.cocomusic.bean.Playlist

/**
 * Author   : D22434
 * version  : 2018/3/1
 * function : 在线歌单新增、删除、修改、重命名
 */
class MyPlaylistEvent(var operate: Int, val playlist: Playlist? = null)
