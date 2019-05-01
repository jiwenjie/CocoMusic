package com.jiwenjie.cocomusic.common

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:
 *  version:1.0
 */
object Constants {
    //歌曲操作区分
    val OP_LOCAL = 0 //没有歌曲下载、删除、修改(后续会有)、添加到歌单
    val OP_ONLINE = 1 //没有歌曲删除、修改、添加到歌单
    val OP_PLAYLIST = 2//修改(后续会有)、添加到歌单

    val WEIBO = "weibo"
    //歌曲类型
    val LOCAL = "local"
    val QQ = "qq"
    val XIAMI = "xiami"
    val BAIDU = "baidu"
    val NETEASE = "netease"

    //特殊歌单类型
    val PLAYLIST_LOVE_ID = "love"
    val PLAYLIST_HISTORY_ID = "history"
    val PLAYLIST_LOCAL_ID = "local"
    val PLAYLIST_QUEUE_ID = "queue"
    val PLAYLIST_DOWNLOAD_ID = "download"
    //百度歌单
    val PLAYLIST_BD_ID = "playlist_bd"
    //网易云歌单
    val PLAYLIST_WY_ID = "playlist_wy"
    //QQ歌单
    val PLAYLIST_QQ_ID = "playlist_qq"
    //虾米歌单
    val PLAYLIST_XIA_MI_ID = "playlist_xm"
    //在线歌单
    val PLAYLIST_CUSTOM_ID = "custom_online"
    val PLAYLIST_SEARCH_ID = "playlist_search"
    val PLAYLIST_IMPORT_ID = "playlist_import"
    //百度电台列表
    val BAIDU_RADIO_LIST = "baidu_radio_list"
    val NETEASE_ARITIST_LIST = "netease_artist_list"

    /**
     * 搜索过滤
     */
    val SP_KEY_SEARCH_FILTER_NETEASE = "sp_netease"
    val SP_KEY_SEARCH_FILTER_QQ = "sp_netease"
    val SP_KEY_SEARCH_FILTER_XIAMI = "sp_xiami"
    val SP_KEY_SEARCH_FILTER_BAIDU = "sp_baidu"
    val SP_KEY_SEARCH_FILTER_ = "sp_netease"
    val SP_KEY_SONG_QUALITY = "song_quality"

    //歌单操作
    val PLAYLIST_ADD = 0
    val PLAYLIST_DELETE = 1
    val PLAYLIST_UPDATE = 2
    val PLAYLIST_RENAME = 3

    //QQ APP_ID
    val APP_ID = "101454823"

    //社区后台操作php
    val DEFAULT_URL = "http://119.29.27.116/hkmusic/operate.php"
    val LOGIN_URL = "http://119.29.27.116/hkmusic/login.php"
    val REGISTER_URL = "http://119.29.27.116/hkmusic/register.php"
    val UPLOAD_URL = "http://119.29.27.116/hkmusic/upload_file.php"

    //用户邮箱
    val USER_EMAIL = "email"
    //用户登录密码
    val PASSWORD = "password"
    val TOKEN = "token"
    val TOKEN_TIME = "token_time"
    val LOGIN_STATUS = "login_status"
    //用户名
    val USERNAME = "username"
    //性别
    val USER_SEX = "user_sex"
    //性别
    val USER_IMG = "user_img"
    val USER_COLLEGE = "user_college"
    val USER_MAJOR = "user_major"
    val USER_CLASS = "user_class"
    val NICK = "nick"
    val PHONE = "phone"
    val SECRET = "secret"


    //更新用户信息

    val UPDATE_USER = "updateUserInfo"

    //用户id
    val USER_ID = "user_id"
    //动态id
    val SECRET_ID = "secret_id"
    //内容[动态内容|评论内容]
    val CONTENT = "content"

    //功能
    val FUNC = "func"

    //摇一摇歌曲
    val SONG_ADD = "addSong"
    val SONG = "song"

    //是否是缓存
    val KEY_IS_CACHE = "is_cache"

    //歌单
    val PLAYLIST_ID = "playlist"

    val IS_URL_HEADER = "http"

    val TEXT_PLAIN = "text/plain"

    /**
     * 悬浮窗权限requestCode
     */
    val REQUEST_CODE_FLOAT_WINDOW = 0x123

    //在线音乐
    val FILENAME_MP3 = ".mp3"
    val FILENAME_LRC = ".lrc"
    val MUSIC_LIST_SIZE = 10

    val BASE_URL = "http://musicapi.leanapp.cn/"//"/ting";


    val BASE_MUSIC_URL =
        "http://tingapi.ting.baidu.com/v1/restserver/ting?" + "from=android&version=5.8.2.0&channel=huwei&operator=1&method=baidu.ting.billboard.billCategory&format=json&kflag=2"

    val PLAY_MUSIC_URL =
        "http://tingapi.ting.baidu.com/v1/restserver/ting?" + "method=baidu.ting.song.play&songid=877578"


    val DOWNLOAD_FILENAME = "hkmusic.apk"

    val DEAULT_NOTIFICATION = "notification"
    val TRANSTITION_ALBUM = "transition_album_art"

    /**
     * QQ音乐Api*************************************************
     */
    val BASE_URL_QQ_MUSIC_SEARCH = "http://c.y.qq.com/soso/fcgi-bin/search_cp?"

    val BASE_URL_QQ_MUSIC_URL = "http://dl.stream.qqmusic.qq.com/"
    val BASE_URL_QQ_MUSIC_KEY = "https://c.y.qq.com/base/fcgi-bin/fcg_musicexpress.fcg?"

    /**
     * 虾米音乐Api*************************************************
     */
    val BASE_XIAMI_URL = "http://api.xiami.com/"
    /**
     * 酷狗音乐Api*************************************************
     */
    val BASE_KUGOU_URL = "http://lyrics.kugou.com/"
    /**
     * 百度音乐Api*************************************************
     */
    val BASE_URL_BAIDU_MUSIC = "http://musicapi.qianqian.com/"

    val URL_GET_SONG_INFO = "http://music.baidu.com/data/music/links?songIds="

    /**
     * 在线歌单接口Api*************************************************
     */
    val BASE_PLAYER_URL = " https://player.zzsun.cc/"
    /**
     * 网易云音乐接口
     */
    val BASE_NETEASE_URL = "https://netease.api.zzsun.cc/"
    //bugly app_id
    val BUG_APP_ID = "fd892b37ea"

    /**
     * 关于的GitHub地址
     */
    val ABOUT_MUSIC_LAKE = "https://github.com/caiyonglong/MusicLake"
    val ABOUT_MUSIC_LAKE_ISSUES = "https://github.com/caiyonglong/MusicLake/issues/new"
    val ABOUT_MUSIC_LAKE_PC = "https://github.com/sunzongzheng/music/releases"
    val ABOUT_MUSIC_LAKE_URL = "https://github.com/caiyonglong/MusicLake/blob/develop/README.md"


    /**
     * 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
     */
    val APP_KEY = "3338754271"

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    val REDIRECT_URL = "https://api.weibo.com/oauth2/default.html"

    /**
     * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
     * 详情请查看 Demo 中对应的注释。
     */
    val SCOPE = ""

    //    public static final String SOCKET_URL = "http://39.108.214.63:15003";
    val SOCKET_URL = "https://socket.zzsun.cc"
}