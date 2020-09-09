package com.jiwenjie.cocomusic.ui.contract

import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.ui.contract.base.BaseNormalView
import io.reactivex.Observable

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2020/09/09
 *  desc:
 *  version:1.0
 */
interface FindContract {

    interface Model {
        fun getTopMusic(): Observable<MutableList<Music>?>    // 获取排行榜数据
    }

    interface View: BaseNormalView {
        fun showTopMusic(musicList: MutableList<Music>?)    // 获取本地音乐的数量
    }

    interface Presenter {
        fun getTopMusic()    // 获取排行榜数据

    }
}