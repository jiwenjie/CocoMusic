package com.jiwenjie.cocomusic.ui.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jiwenjie.basepart.mvp.BaseMvpFragment
import com.jiwenjie.basepart.utils.LogUtils
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.bean.MusicListBean
import com.jiwenjie.cocomusic.ui.activity.LocalMusicActivity
import com.jiwenjie.cocomusic.ui.adapter.SongListAdapter
import com.jiwenjie.cocomusic.ui.contract.MineContract
import com.jiwenjie.cocomusic.ui.presenter.MFragmentPresenter
import com.jiwenjie.cocomusic.widget.AddMusicListDialogFragment
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/13
 *  desc:首页 -> 我的 fragment
 *  version:1.0
 */
class MineFragment : BaseMvpFragment<MineContract.View, MFragmentPresenter>(), MineContract.View {

   // 保存获取到的本地音乐，点击的时候把值作为参数传递过去
   private lateinit var localMusic: ArrayList<Music>

   private val createList by lazy { ArrayList<MusicListBean>() }
   private val collectList by lazy { ArrayList<MusicListBean>() }

   private val createAdapter by lazy { SongListAdapter(activity!!, createList) }
   private val collectAdapter by lazy { SongListAdapter(activity!!, collectList) }

   companion object {
      @JvmStatic
      fun newInstance(): MineFragment {
         return MineFragment().apply {
            arguments = Bundle().apply {
               putString("", "")
            }
         }
      }
   }

   override fun initFragment(savedInstanceState: Bundle?) {
      initView()
      initEvent()
   }

   @SuppressLint("ResourceType")
   private fun initView() {
      mineRefreshLyt.run {
         isEnabled = false
//         setProgressViewOffset(true, 50, 200)    //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
//         setSize(SwipeRefreshLayout.DEFAULT)   //设置下拉圆圈的大小，两个值 LARGE， DEFAULT
         setColorSchemeResources(R.color.colorPrimary)
         setDistanceToTriggerSync(200)
         setProgressBackgroundColorSchemeColor(ContextCompat.getColor(activity!!, R.color.white))
      }

      createMusicListRv.adapter = createAdapter
      createMusicListRv.layoutManager = LinearLayoutManager(activity)

      collectMusicListRv.adapter = collectAdapter
      collectMusicListRv.layoutManager = LinearLayoutManager(activity)
   }

   private fun initEvent() {
      mine_localLyt.setOnClickListener {
         // 点击跳转本地音乐
         LocalMusicActivity.runActivity(activity!!, beanList = localMusic)
      }
      createMusicListLyt.setOnClickListener {
         // first start animation, display rv when the animation complete
         displayMusicList(createListRArrow, createMusicListRv)
      }
      collectMusicListLyt.setOnClickListener {
         displayMusicList(collectListRArrow, collectMusicListRv)
      }
      createMusicListImg.setOnClickListener {
         addMusicList()    // 点击弹出提示框创建歌单
      }

      // 列表的点击事件
      createAdapter.setOnItemClickListener { position, view ->
         ToastUtils.showToast(activity!!, "点击我创建的歌单")
      }
      createAdapter.setOnRightMenuClickListener(object : SongListAdapter.OnRightMenuClickListener {
         override fun onMenuClick(bean: MusicListBean) {

         }
      })

      collectAdapter.setOnItemClickListener { position, view ->
         ToastUtils.showToast(activity!!, "点击我收藏的歌单")
      }
      collectAdapter.setOnRightMenuClickListener(object : SongListAdapter.OnRightMenuClickListener {
         override fun onMenuClick(bean: MusicListBean) {

         }
      })
   }

   override fun loadData() {
      mPresenter.getLocalMusicSize()      // 获取本地音乐的数量和内容
      mPresenter.getRecentOpen()          // 获取最近播放的歌曲
      mPresenter.getMyRadio()             // 获取我的电台
      mPresenter.getMyCollect()           // 获取我的收藏
      // todo 下载管理先不做
      mPresenter.getCreateMusicList()     // 获取创建的歌单
      mPresenter.getCollectMusicList()    // 获取收藏的歌单
   }

   // get the local music
   override fun showLocalMusicSize(musicList: MutableList<Music>?) {
      LogUtils.e("MusicSize: ${musicList?.size}")
//      mineRefreshLyt.isRefreshing = false
      localMusicSizeText.text = String.format("(%d)", if (musicList.isNullOrEmpty() || musicList.size == 0) 0 else musicList.size)   // 设置本地音乐的数量
      localMusic = musicList as ArrayList<Music>
   }

   // show the recently song
   override fun showRecentOpen(musicList: MutableList<Music>?) {
      // 最近播放歌曲数量做一个限制，最多显示 100 首
   }

   override fun managerDownload() {

   }

   override fun showMyRadio() {

   }

   override fun showMyCollect() {

   }

   override fun showCreateMusicList(beanList: ArrayList<MusicListBean>) {
      if (!beanList.isNullOrEmpty()) {
         createAdapter.addAllData(beanList)
      }
   }

   override fun showCollectMusicList(beanList: ArrayList<MusicListBean>) {
      if (!beanList.isNullOrEmpty()) {
         collectAdapter.addAllData(beanList)
      }
   }

   // first start animation, display the rv when the animation end
   @SuppressLint("ObjectAnimatorBinding")
   private fun displayMusicList(target: View, currentRv: View) {
      val objAnim : ObjectAnimator
      if (currentRv.visibility == View.GONE) {  // 还原的动画
         objAnim = ObjectAnimator.ofFloat(target, "rotation", 0f, 90f)
      } else {          // 点击后的动画
         objAnim = ObjectAnimator.ofFloat(target, "rotation", 90f, 0f)
      }
      objAnim.duration = 300
      objAnim.addListener(object : Animator.AnimatorListener {
         override fun onAnimationRepeat(animation: Animator?) {
         }

         override fun onAnimationCancel(animation: Animator?) {
         }

         override fun onAnimationStart(animation: Animator?) {
         }

         override fun onAnimationEnd(animation: Animator?) {
            LogUtils.e("the animation complete")
            // when the animator end, rv display
            if (currentRv.visibility == View.GONE) {
               LogUtils.e("needDisplayRv Visible")
               currentRv.visibility = View.VISIBLE
            } else {
               LogUtils.e("needDisplayRv GONE")
               currentRv.visibility = View.GONE
            }
         }
      })
      objAnim.start()
   }

   override fun showLoading() {
      mineRefreshLyt.isRefreshing = true
   }

   override fun dismissProgress() {
      mineRefreshLyt.isRefreshing = false
   }

   /**
    * add one musicList
    */
   fun addMusicList() {
      AddMusicListDialogFragment.showDialogFragment(fragmentManager!!)
   }

   override fun initPresenter(): MFragmentPresenter = MFragmentPresenter(this)

   override fun getLayoutId(): Int = R.layout.fragment_mine
}