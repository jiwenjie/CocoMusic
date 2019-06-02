package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.jiwenjie.basepart.adapters.BaseFragmentPagerAdapter
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.test.TestFragment
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
      val fragmentList= ArrayList<Fragment>().apply {
         add(SingleMusicFragment.newInstance(intent.getParcelableArrayListExtra(KEY_BEAN_LIST)))
         add(TestFragment.newInstance("second"))
         add(TestFragment.newInstance("third"))
         add(TestFragment.newInstance("four"))
      }

      val titleList = ArrayList<String>().apply {
         add("单曲")
         add("歌手")
         add("专辑")
         add("文件夹")
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
