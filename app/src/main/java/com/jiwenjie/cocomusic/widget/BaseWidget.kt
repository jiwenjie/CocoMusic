package com.jiwenjie.cocomusic.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.widget.RemoteViews
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.playservice.MusicPlayerService
import kotlin.math.acos

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/03
 *  desc:
 *  version:1.0
 */
abstract class BaseWidget : AppWidgetProvider() {

    protected val REQUEST_NEXT = 1
    protected val REQUEST_PREV = 2
    protected val REQUEST_PLAYPAUSE = 3

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        onUpdate(context!!, appWidgetManager!!, appWidgetIds!!, null)
    }

    private fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, extras: Bundle?) {
        val serviceName = ComponentName(context, MusicPlayerService::class.java)
        val remoteViews = RemoteViews(context.packageName, getLayoutRes())
        try {
            onViewsUpdate(context, remoteViews, serviceName, extras)
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        LogUtils.e("BaseWidget 接收到广播---------$action")
        if (action!!.startsWith("com.jiwenjie.cocomusic.")) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context!!.packageName, this.javaClass.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            onUpdate(context, appWidgetManager, appWidgetIds, intent.extras)
        } else {
            super.onReceive(context, intent)
        }
    }

    internal abstract fun onViewsUpdate(context: Context, remoteViews: RemoteViews, serviceName: ComponentName, extras: Bundle?)
}






















