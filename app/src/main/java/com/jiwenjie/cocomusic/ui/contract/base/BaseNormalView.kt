package com.jiwenjie.cocomusic.ui.contract.base

import com.jiwenjie.basepart.mvp.BaseMvpViewImpl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/27
 *  desc:
 *  version:1.0
 */
interface BaseNormalView : BaseMvpViewImpl {
   fun showLoading()
   fun dismissProgress()
}
