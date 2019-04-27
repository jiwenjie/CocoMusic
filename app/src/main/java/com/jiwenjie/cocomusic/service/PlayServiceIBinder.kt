package com.jiwenjie.cocomusic.service

import android.content.Context
import com.jiwenjie.cocomusic.aidl.PlayControlImpl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/25
 *  desc:
 *  version:1.0
 */
class PlayServiceIBinder(private val mContext: Context) : PlayControlImpl(mContext)