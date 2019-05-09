package com.jiwenjie.cocomusic.ui.contract.base

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/09
 *  desc:
 *  version:1.0
 */
interface BasePresenter<T : BaseView> {
   fun attachView(view: T)

   fun detachView()
}