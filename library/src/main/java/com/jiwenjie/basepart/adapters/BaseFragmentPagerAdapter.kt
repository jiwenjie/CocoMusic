package com.jiwenjie.basepart.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/04
 *  desc:
 *  version:1.0
 */
class BaseFragmentPagerAdapter(fragmentManager: FragmentManager, fragmentList: ArrayList<Fragment>, titles: ArrayList<String>)
    : FragmentPagerAdapter(fragmentManager) {

    private var mFragments = fragmentList
    private var mTitles = titles

    override fun getItem(position: Int): Fragment = mFragments[position]

    override fun getCount(): Int = mTitles.size

    override fun getPageTitle(position: Int): CharSequence? =
        if (mTitles.isEmpty()) super.getPageTitle(position) else mTitles[position]
}