package com.jiwenjie.cocomusic.ui.contract.base

import android.content.Context
import com.jiwenjie.basepart.mvp.BaseMvpViewImpl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/09
 *  desc:
 *  version:1.0
 */
interface BaseView : BaseMvpViewImpl {
   fun getContext(): Context
}
