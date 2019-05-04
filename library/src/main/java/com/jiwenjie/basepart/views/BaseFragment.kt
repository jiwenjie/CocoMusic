package com.jiwenjie.basepart.views

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jaeger.library.StatusBarUtil
import com.jiwenjie.basepart.R
import com.jiwenjie.basepart.utils.LogUtils

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2018/12/14
 *  desc:fragment 基类
 *  version:1.0
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogUtils.e("onCreateView()")
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.e("onViewCreated()")
        initFragment(savedInstanceState)
        StatusBarUtil.setColor(activity, ContextCompat.getColor(activity!!, R.color.colorPrimary), 0)
        loadData()
        setListener()
        handleRxBus()
    }

    protected open fun loadData() {}

    protected abstract fun getLayoutId(): Int

    protected abstract fun initFragment(savedInstanceState: Bundle?)

    protected open fun setListener() {}

    protected open fun handleRxBus() {}

    fun startActivity(clazz: Class<*>) = activity!!.startActivity(Intent(activity, clazz))
}