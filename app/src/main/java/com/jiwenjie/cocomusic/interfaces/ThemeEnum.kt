package com.jiwenjie.cocomusic.interfaces

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:
 *  version:1.0
 */
enum class ThemeEnum {
    //白色
    WHITE,

    //黑色
    DARK,

    //随专辑图片变化
    VARYING;

    fun reversal(theme: ThemeEnum): ThemeEnum {
        return if (theme == WHITE || theme == VARYING) {
            DARK
        } else {
            WHITE
        }
    }
}