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
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.test.TestFragment
import com.jiwenjie.cocomusic.ui.contract.MainContract
import com.jiwenjie.cocomusic.ui.fragment.MineFragment
import com.jiwenjie.cocomusic.ui.presenter.MainPresenter
import com.jiwenjie.cocomusic.utils.ObjAnimatorUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_left_layout.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import java.util.concurrent.TimeUnit

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/12
 *  desc:
 *  version:1.0
 */
@Suppress("DEPRECATION")
class MainActivity : PlayBaseMvpActivity<MainContract.View, MainPresenter>(), MainContract.View {

   private val CURRENT_ITEM_MINE = 0     // mine
   private val CURRENT_ITEM_FIND = 1     // find
   private val CURRENT_ITEM_FRIEND = 2   // friend
   private val CURRENT_ITEM_VIDEO = 3    // video

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
      // 设置了该方法后即可使得侧滑栏高度充满全屏
      initView()
      initEvent()
      // 设置默认的显示界面
      updateUI(-1, currentIndex)
   }

   private fun initView() {
      // main part
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

      // drawlerLyt start
      avatarImg.setOnClickListener {
         ToastUtils.showToast(getActivity(), "暂未实现")
      }
      setTimeStopLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.setTimeStopPlay(getActivity())
                 }
      }
      scanLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.openScan(getActivity())
                 }
      }
      myFriendLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.showMyFriend(getActivity())
                 }
      }
      changeThemeLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.changeAppTheme(getActivity())
                 }
      }
      alarmColckLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.openMusicAlarmClock(getActivity())
                 }
      }
      cloudDiskLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.openMusicCloud(getActivity())
                 }
      }
      couponLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                 }
         ToastUtils.showToast(getActivity(), "暂未实现")
      }
      joinUsLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                 }
         ToastUtils.showToast(getActivity(), "暂未实现")
      }
      broadcastLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                 }
         ToastUtils.showToast(getActivity(), "暂未实现")
      }
      nightTypeLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.setNightType(getActivity())
                 }
      }
      settingLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.openSetting(getActivity())
                 }
      }
      exitLyt.setOnClickListener {
         drawer_layout.closeDrawers()
         Observable.timer(300, TimeUnit.MILLISECONDS)
                 .subscribe {
                    mPresenter.exitApplication()
                 }
      }
      // drawlerLyt end

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
      currentIndex = currentItem

      if (lastIndex != -1 && lastIndex == currentIndex) return

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
            ObjAnimatorUtils.startAnimObj(common_mine, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_FIND -> {
            ObjAnimatorUtils.startAnimObj(common_find, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_FRIEND -> {
            ObjAnimatorUtils.startAnimObj(common_friend, "textSize", "textColor", isSelect)
         }
         CURRENT_ITEM_VIDEO -> {
            ObjAnimatorUtils.startAnimObj(common_video, "textSize", "textColor", isSelect)
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

   override fun initPresenter(): MainPresenter = MainPresenter(this)

   override fun getLayoutId(): Int = R.layout.activity_main
}