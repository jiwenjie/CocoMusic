package com.jiwenjie.cocomusic.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.KeyEvent
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.basepart.views.BaseActivity
import com.jiwenjie.cocomusic.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/12
 *  desc:
 *  version:1.0
 */
class MainActivity : BaseActivity() {

   companion object {
      @JvmStatic
      fun runActivity(activity: Activity) {
         val intent = Intent(activity, MainActivity::class.java)
         activity.startActivity(intent)
      }
   }

   override fun initActivity(savedInstanceState: Bundle?) {
      initDrawer()
   }

   private fun initDrawer() {
      activity_drawerLyt.run {
         // 参数：开启抽屉的activity、DrawerLayout的对象、toolbar按钮打开关闭的对象、
         // 描述open drawer、描述close drawer
         val toggle = ActionBarDrawerToggle(
                 this@MainActivity,
                 this,
                 toolbar,
                 R.string.navigation_drawer_open,
                 R.string.navigation_drawer_close
         )
         addDrawerListener(toggle)
         // 添加抽屉按钮，通过点击按钮实现打开和关闭功能; 如果不想要抽屉按钮，只允许在侧边边界拉出侧边栏，可以不写此行代码
         toggle.syncState()
         // 设置按钮的动画效果; 如果不想要打开关闭抽屉时的箭头动画效果，可以不写此行代码
         setDrawerListener(toggle)
      }
   }

   // 双击退出程序
   var prePressTime = 0.toLong()

   override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
         if (System.currentTimeMillis() - prePressTime > 2000) {
            prePressTime = System.currentTimeMillis()
            ToastUtils.showToast(this, resources.getString(R.string.exit_tip))
         } else {
            finish()
            android.os.Process.killProcess(android.os.Process.myUid())
            System.exit(0)
         }
         return false
      } else {
         // 点击音量键加减的时候也会响应该方法，所以在这里处理，防止点击音量键会导致应用退出
         return super.onKeyDown(keyCode, event)
      }
   }

   override fun getLayoutId(): Int = R.layout.activity_main
}