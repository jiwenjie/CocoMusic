package com.jiwenjie.basepart.views

import android.os.Bundle
import android.support.v4.app.Fragment

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/06/05
 *  desc:
 *  version:1.0
 */
abstract class BaseLazyFragment : Fragment() {

    protected var isLazyLoaded = false

    protected var isPrepared = false

    protected var isNeedRefresh = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onInit()
        isPrepared = true
        lazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        lazyLoad()
    }

    private fun lazyLoad() {
        if (userVisibleHint && isPrepared) {
            if (!isLazyLoaded) {
                onLazyLoad(true)
                isLazyLoaded = true
                isNeedRefresh = false
            } else if (isNeedRefresh) {
                onLazyLoad(false)
                isNeedRefresh = false
            }
        }
    }

    open fun lazyRefresh() {
        isNeedRefresh = true
        lazyLoad()
    }

    abstract fun onInit()

    abstract fun onLazyLoad(isFirst: Boolean)
}