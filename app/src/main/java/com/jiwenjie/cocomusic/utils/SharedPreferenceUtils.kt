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
object SharedPreferenceUtils {
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

    val KEY_ISPLAY = "key_isplay"     // 是否播放过音乐，如果播放过则显示底部菜单，否则隐藏

    var playPosition: Int
        get() = getIntMethod(PLAY_POSITION, -1)
        set(position) = setIntMethod(PLAY_POSITION, position)


    val currentSongId: String?
        get() = getStringMethod(MUSIC_ID, "")

    val position: Long
        get() = getLongMethod(POSITION, 0L)

    val playMode: Int
        get() = getIntMethod(PLAY_MODE, 0)

    val splashUrl: String?
        get() = getStringMethod(SPLASH_URL, "")

    val wifiMode: Boolean
        get() = getBooleanMethod(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), false)

    val isShowLyricView: Boolean
        get() = getBooleanMethod(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), false)


    val isNightMode: Boolean
        get() = getBooleanMethod(NIGHT_MODE, false)


    val fontSize: Int
        get() = getIntMethod(DESKTOP_LYRIC_SIZE, 30)

    val fontColor: Int
        get() = getIntMethod(DESKTOP_LYRIC_COLOR, Color.RED)

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(CocoApp.contextInstance)

    fun saveCurrentSongId(mid: String) {
        setStringMethod(MUSIC_ID, mid)
    }

    fun savePosition(id: Long) {
        setLongMethod(POSITION, id)
    }

    fun savePlayMode(mode: Int) {
        setIntMethod(PLAY_MODE, mode)
    }

    fun saveSplashUrl(url: String) {
        setStringMethod(SPLASH_URL, url)
    }

    fun saveWifiMode(enable: Boolean) {
        setBooleanMethod(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), enable)
    }

    fun showLyricView(enable: Boolean) {
        setBooleanMethod(CocoApp.contextInstance.getString(R.string.setting_key_mobile_wifi), enable)
    }

    fun saveNightMode(on: Boolean) {
        setBooleanMethod(NIGHT_MODE, on)
    }

    fun saveFontSize(size: Int) {
        setIntMethod(DESKTOP_LYRIC_SIZE, size)
    }

    fun saveFontColor(color: Int) {
        setIntMethod(DESKTOP_LYRIC_COLOR, color)
    }

    /**
     * -------------------------------------------------------
     * 底层操作
     *
     * 公共方法
     * -------------------------------------------------------
     */
    fun getBooleanMethod(key: String, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun setBooleanMethod(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getFloatMethod(key: String, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun setFloatMethod(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun getIntMethod(key: String, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun setIntMethod(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getLongMethod(key: String, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun setLongMethod(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getStringMethod(key: String, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun setStringMethod(key: String, value: String?) {
        preferences.edit().putString(key, value).apply()
    }
}
