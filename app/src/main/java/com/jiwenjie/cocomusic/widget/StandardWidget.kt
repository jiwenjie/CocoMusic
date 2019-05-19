package com.jiwenjie.cocomusic.widget

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.RemoteViews
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.common.Extras
import com.jiwenjie.cocomusic.common.NavigationHelper
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService
import com.jiwenjie.cocomusic.utils.CoverLoader

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:
 *  version:1.0
 */
class StandardWidget : BaseWidget() {

    private var isFirstCreate = true

    override fun onViewsUpdate(context: Context, remoteViews: RemoteViews, serviceName: ComponentName, extras: Bundle?) {
        LogUtils.e("BaseWidget 接收到广播---------- onViewsUpdate")
        if (isFirstCreate) {
            remoteViews.setOnClickPendingIntent(R.id.iv_next, PendingIntent.getService(
                context,
                REQUEST_NEXT,
                Intent(context, MusicPlayerService::class.java)
                    .setAction(MusicPlayerService.ACTION_NEXT)
                    .setComponent(serviceName),
                0))     // 下一曲
            remoteViews.setOnClickPendingIntent(R.id.iv_prev, PendingIntent.getService(
                context,
                REQUEST_PREV,
                Intent(context, MusicPlayerService::class.java)
                    .setAction(MusicPlayerService.ACTION_PREV)
                    .setComponent(serviceName),
                0))     // 上一曲
            remoteViews.setOnClickPendingIntent(R.id.iv_play_pause, PendingIntent.getService(
                context,
                REQUEST_PLAYPAUSE,
                Intent(context, MusicPlayerService::class.java)
                    .setAction(MusicPlayerService.ACTION_PLAY_PAUSE)
                    .setComponent(serviceName),
                PendingIntent.FLAG_UPDATE_CURRENT))     // 暂停

            remoteViews.setOnClickPendingIntent(R.id.iv_cover, PendingIntent.getActivity(
                context,
                0,
                NavigationHelper.getNowPlayingIntent(context)
                    .setComponent(serviceName),
                PendingIntent.FLAG_UPDATE_CURRENT))

            remoteViews.setOnClickPendingIntent(R.id.iv_lyric, PendingIntent.getService(
                context,
                0,
                NavigationHelper.getLyricIntent(context)
                    .setComponent(serviceName),
                PendingIntent.FLAG_UPDATE_CURRENT
            ))
            isFirstCreate = false
        }

        if (extras != null) {
            remoteViews.setImageViewResource(R.id.iv_play_pause,
                if (extras.getBoolean(Extras.PLAY_STATUS, false)) R.drawable.ic_pause else R.drawable.ic_play)
        }

        if (MusicPlayerService.getInstance() != null) {
            val music = MusicPlayerService.getInstance()!!.getPlayingMusic() ?: return
            remoteViews.setTextViewText(R.id.tv_title, music.title + " - " + music.artist)
            CoverLoader.loadImageViewByMusic(context, music, object : CoverLoader.BitmapCallBack {
                override fun showBitmap(bitmap: Bitmap?) {
                    if (bitmap != null) {
                        remoteViews.setImageViewBitmap(R.id.iv_cover, bitmap)
                    } else {
                        remoteViews.setImageViewResource(R.id.iv_cover, R.drawable.default_cover)
                    }
                }
            })
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        LogUtils.e("BaseWidget 接收到广播--------------- 第一次创建")
        isFirstCreate = true
        val intent = Intent(context, MusicPlayerService::class.java)
        context.startService(intent)
    }

    override fun getLayoutRes(): Int = R.layout.widget_standard
}




























