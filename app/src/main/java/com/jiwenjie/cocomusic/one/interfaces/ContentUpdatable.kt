package com.jiwenjie.cocomusic.one.interfaces

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/27
 *  desc:内容更新
 *  version:1.0
 */
interface ContentUpdatable {
    /**
     * 进行数据更新，该方法中应对数据进行判空处理，当没有数据时应实现并调用[.noData]
     * 进行处理。
     */
    fun update(obj: Any, statusChanged: OnUpdateStatusChanged)

    /**
     * 没有数据
     */
    fun noData()
}