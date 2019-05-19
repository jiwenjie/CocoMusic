package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.KeyEvent
import com.jaeger.library.StatusBarUtil
import com.jiwenjie.basepart.adapters.BaseFragmentPagerAdapter
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.test.TestFragment
import com.jiwenjie.cocomusic.ui.fragment.MineFragment
import com.jiwenjie.cocomusic.utils.ObjAnimatorUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/12
 *  desc:
 *  version:1.0
 */
@Suppress("DEPRECATION")
class MainActivity : PlayBaseActivity() {

   private val CURRENT_ITEM_MINE = 0     // 我的
   private val CURRENT_ITEM_FIND = 1     // 发现
   private val CURRENT_ITEM_FRIEND = 2   // 朋友
   private val CURRENT_ITEM_VIDEO = 3    // 视频

   private var currentIndex = 0     // 标识 toolbar 的当前下标

   companion object {
      @JvmStatic
      fun runActivity(activity: Activity) {
         val intent = Intent(activity, MainActivity::class.java)
         activity.startActivity(intent)
      }
   }

   /**
    * 注意 super.initActivity() 必须调用
    */
   override fun initActivity(savedInstanceState: Bundle?) {
      StatusBarUtil.setColorForDrawerLayout(getActivity(), drawer_layout,
              ContextCompat.getColor(getActivity(), R.color.colorPrimary), 0)
      super.initActivity(savedInstanceState)
      // 设置了该方法后即可使得侧滑栏高度充满全屏
      initView()
      initEvent()
      // 设置默认的显示界面
      updateUI(-1, currentIndex)
   }

   private fun initView() {

      val fragmentList = ArrayList<Fragment>().apply {
         add(MineFragment.newInstance())
         add(TestFragment.newInstance("two"))
         add(TestFragment.newInstance("three"))
         add(TestFragment.newInstance("four"))
      }

      val titleList = ArrayList<String>().apply {
         add("mine")
         add("find")
         add("friend")
         add("video")
      }

      viewPager.adapter = BaseFragmentPagerAdapter(supportFragmentManager, fragmentList, titleList)
   }

   private fun initEvent() {
      common_menu.setOnClickListener {
         drawer_layout.openDrawer(Gravity.START)
      }

      common_mine.setOnClickListener {
         // 我的
         updateUI(currentIndex, CURRENT_ITEM_MINE)
      }
      common_find.setOnClickListener {
         // 发现
         updateUI(currentIndex, CURRENT_ITEM_FIND)
      }
      common_friend.setOnClickListener {
         // 朋友
         updateUI(currentIndex, CURRENT_ITEM_FRIEND)
      }
      common_video.setOnClickListener {
         // 视频
         updateUI(currentIndex, CURRENT_ITEM_VIDEO)
      }

      common_search.setOnClickListener {
         // 点击搜索按钮，跳转搜索界面
         SearchActivity.runActivity(this)
      }

      viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
         override fun onPageScrollStateChanged(p0: Int) {
         }

         override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
         }

         override fun onPageSelected(position: Int) {
            updateUI(currentIndex, position)
         }
      })
   }

   private fun updateUI(lastIndex: Int, currentItem: Int) {
      if (lastIndex != -1 && lastIndex == currentIndex) return
      currentIndex = currentItem

      // 设置被选中的 item 的选中动画
      changeItem(currentItem, isSelect = true)
      // 设置之前选中的 item 的还原动画
      changeItem(lastIndex, isSelect = false)

      viewPager.setCurrentItem(currentItem, true)
   }

   // 改变选择 item 的方法
   private fun changeItem(index: Int, isSelect: Boolean) {
      when (index) {
         CURRENT_ITEM_MINE -> {
            ObjAnimatorUtils.startAnim(common_mine, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_FIND -> {
            ObjAnimatorUtils.startAnim(common_find, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_FRIEND -> {
            ObjAnimatorUtils.startAnim(common_friend, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_VIDEO -> {
            ObjAnimatorUtils.startAnim(common_video, "textSize", "textColor", isSelect)
         }
      }
   }

   override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
      if (keyCode == KeyEvent.KEYCODE_BACK) {      // 设置点击返回桌面而不是退出应用
         val home = Intent(Intent.ACTION_MAIN).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            addCategory(Intent.CATEGORY_HOME)
         }
         startActivity(home)
         return true
      }
      return super.onKeyDown(keyCode, event)
   }

   override fun getLayoutId(): Int = R.layout.activity_main
}