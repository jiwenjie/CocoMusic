package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.view.menu.MenuBuilder
import android.view.Menu
import android.view.MenuItem
import com.jiwenjie.basepart.adapters.BaseFragmentPagerAdapter
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.test.TestFragment
import com.jiwenjie.cocomusic.ui.fragment.SingerFragment
import com.jiwenjie.cocomusic.ui.fragment.SingleMusicFragment
import kotlinx.android.synthetic.main.activity_local.*
import kotlin.collections.ArrayList

class LocalMusicActivity : PlayBaseActivity() {

   companion object {
      private const val KEY_BEAN_LIST = "key_bean_list"

      @JvmStatic
      fun runActivity(activity: Activity, beanList: ArrayList<Music>) {
         activity.apply {
            intent.apply {
               setClass(activity, LocalMusicActivity::class.java)
               putExtra(KEY_BEAN_LIST, beanList)
            }
            startActivity(intent)
         }
      }
   }

   override fun initActivity(savedInstanceState: Bundle?) {
      super.initActivity(savedInstanceState)
      initView()
      initEvent()
   }

   private fun initView() {
      // toolbar 加载 menu 有两种方式，我这里用的是第一种，必须增加这条语句。
      // 第二种是用 toolbar 的 setMenu 方法设置则不必
      setSupportActionBar(localToolbar)

      val fragmentList = ArrayList<Fragment>().apply {
         add(SingleMusicFragment.newInstance(intent.getParcelableArrayListExtra(KEY_BEAN_LIST)))
         add(SingerFragment.newInstance())
         add(TestFragment.newInstance("third"))
         add(TestFragment.newInstance("four"))
      }

      val titleList = ArrayList<String>().apply {
         add(resources.getString(R.string.tab_single_music))
         add(resources.getString(R.string.tab_singer))
         add(resources.getString(R.string.tab_album))
         add(resources.getString(R.string.tab_folder))
      }

      localTabLyt.setupWithViewPager(localViewPager)
      localViewPager.adapter = BaseFragmentPagerAdapter(supportFragmentManager, fragmentList, titleList)
      localViewPager.offscreenPageLimit = fragmentList.size
   }

   private fun initEvent() {
      localToolbar.setNavigationOnClickListener {
         finish()
      }
   }

   /**
    * 把 toolbar 中的 menu icon 和 title 同时显示出来
    */
   override fun onPrepareOptionsMenu(menu: Menu): Boolean {
      if (menu.javaClass == MenuBuilder::class.java) {
         try {
            val m = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
            m.isAccessible = true
            m.invoke(menu, true)
         } catch (e: Exception) {
            e.printStackTrace()
         }
      }
      return super.onPrepareOptionsMenu(menu)
   }

   /**
    * 创建加载 Toolbar 的菜单
    */
   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.local_music_menu, menu)
      return true
   }

   /**
    * set toolbar menu click
    */
   override fun onOptionsItemSelected(item: MenuItem?): Boolean {
      when (item?.itemId) {
         R.id.action_search -> {
            LogUtils.e("action: search")
         }
         R.id.action_scanMusic -> {
            LogUtils.e("action: scanMusic")
         }
         R.id.action_selectSort -> {
            LogUtils.e("action: selectSort")
         }
         R.id.action_getLyric -> {
            LogUtils.e("action: getLyric")
         }
         R.id.action_upgradeQuailty -> {
            LogUtils.e("action: upgradeQuailty")
         }
      }
      return super.onOptionsItemSelected(item)
   }

   override fun getLayoutId(): Int = R.layout.activity_local
}
