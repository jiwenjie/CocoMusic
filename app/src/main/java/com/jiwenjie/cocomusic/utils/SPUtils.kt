package com.jiwenjie.cocomusic.utils

import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import com.jiwenjie.cocomusic.CocoApp
import com.jiwenjie.cocomusic.R

/**
 * 作者：yonglong on 2016/8/12 16:03
 * 邮箱：643872807@qq.com
 * 版本：2.5
 * 内部存儲工具類
 */
object SPUtils {
    /**
     * 第一次进入应用
     */
    val SP_KEY_FIRST_COMING = "first_coming"
    val SP_KEY_FIRST_INIT_DATABASE = "first_init_database"
    val SP_KEY_NOTICE_CODE = "notice_code"

    /**
     * 桌面歌词锁定
     */
    val SP_KEY_FLOAT_LYRIC_LOCK = "float_lyric_lock"


    private val MUSIC_ID = "music_id"
    private val PLAY_POSITION = "play_position"
    private val PLAY_MODE = "play_mode"
    private val SPLASH_URL = "splash_url"
    private val WIFI_MODE = "wifi_mode"
    private val LYRIC_MODE = "lyric_mode"
    private val NIGHT_MODE = "night_mode"
    private val POSITION = "position"
    private val DESKTOP_LYRIC_SIZE = "desktop_lyric_size"
    private val DESKTOP_LYRIC_COLOR = "desktop_lyric_color"
    val QQ_OPEN_ID = "qq_open_id"
    val QQ_ACCESS_TOKEN = "qq_access_token"
    val QQ_EXPIRES_IN = "expires_in"

    var playPosition: Int
        get() = getAnyByKey(PLAY_POSITION, -1)
        set(position) = putAnyCommit(PLAY_POSITION, position)


    val currentSongId: String?
        get() = getAnyByKey(MUSIC_ID, "")

    val position: Long
        get() = getAnyByKey(POSITION, 0L)

    val playMode: Int
        get() = getAnyByKey(PLAY_MODE, 0)

    val splashUrl: String?
        get() = getAnyByKey(SPLASH_URL, "")

    val wifiMode: Boolean
        get() = getAnyByKey(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), false)

    val isShowLyricView: Boolean
        get() = getAnyByKey(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), false)


    val isNightMode: Boolean
        get() = getAnyByKey(NIGHT_MODE, false)


    val fontSize: Int
        get() = getAnyByKey(DESKTOP_LYRIC_SIZE, 30)

    val fontColor: Int
        get() = getAnyByKey(DESKTOP_LYRIC_COLOR, Color.RED)

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(CocoApp.contextInstance)

    fun saveCurrentSongId(mid: String) {
        putAnyCommit(MUSIC_ID, mid)
    }

    fun savePosition(id: Long) {
        putAnyCommit(POSITION, id)
    }

    fun savePlayMode(mode: Int) {
        putAnyCommit(PLAY_MODE, mode)
    }

    fun saveSplashUrl(url: String) {
        putAnyCommit(SPLASH_URL, url)
    }

    fun saveWifiMode(enable: Boolean) {
        putAnyCommit(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), enable)
    }

    fun showLyricView(enable: Boolean) {
        putAnyCommit(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), enable)
    }

    fun saveNightMode(on: Boolean) {
        putAnyCommit(NIGHT_MODE, on)
    }

    fun saveFontSize(size: Int) {
        putAnyCommit(DESKTOP_LYRIC_SIZE, size)
    }


    fun saveFontColor(color: Int) {
        putAnyCommit(DESKTOP_LYRIC_COLOR, color)
    }

    /**
     * -------------------------------------------------------
     *
     * 底层操作
     * -------------------------------------------------------
     */
    fun getAnyByKey(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun putAnyCommit(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getAnyByKey(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun putAnyCommit(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun getAnyByKey(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun putAnyCommit(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getAnyByKey(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun putAnyCommit(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getAnyByKey(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun putAnyCommit(key: String, value: String?) {
        preferences.edit().putString(key, value).apply()
    }
}
