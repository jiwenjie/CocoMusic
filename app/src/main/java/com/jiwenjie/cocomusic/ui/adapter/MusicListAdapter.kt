package com.jiwenjie.cocomusic.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import com.jiwenjie.basepart.adapters.BaseRecyclerAdapter
import com.jiwenjie.basepart.utils.ToastUtils
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.event.MetaChangedEvent
import com.jiwenjie.cocomusic.play.playservice.PlayManager
import com.jiwenjie.cocomusic.utils.CommonUtils
import com.jiwenjie.cocomusic.utils.CoverLoader
import kotlinx.android.synthetic.main.activity_music_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/05
 *  desc:本地歌曲 item
 *  version:1.0
 */
class MusicListAdapter(context: Context, beanList: ArrayList<Music>)
   : BaseRecyclerAdapter<Music>(context, beanList) {

   override fun convertView(itemView: View, data: Music, position: Int) {
      CoverLoader.loadImageView(mContext, data.coverUri!!, itemView.iv_cover)
      itemView.tv_title.text = CommonUtils.getTitle(data.title)

      // 音质图标显示
      val quality = when {
         data.sq -> ContextCompat.getDrawable(mContext, R.drawable.sq_icon)
         data.hq -> ContextCompat.getDrawable(mContext, R.drawable.hq_icon)
         else -> null
      }
      quality?.let {
         quality.setBounds(0, 0, quality.minimumWidth + mContext.dip(2), quality.minimumHeight)
         itemView.tv_artist.setCompoundDrawables(quality, null, null, null)
      }
      // 设置歌手专辑名
      itemView.tv_artist.text = CommonUtils.getArtistAndAlbum(data.artist!!, data.album!!)
      // 设置播放状态
      if (PlayManager.getPlayingId() == data.mid) {
         itemView.v_playing.visibility = View.VISIBLE
         itemView.tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.app_green))
         itemView.tv_artist.setTextColor(ContextCompat.getColor(mContext, R.color.app_green))

//         recyclerView.scrollToPosition(holder.adapterPosition)
      } else {
         itemView.v_playing.visibility = View.GONE
         itemView.tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.dark))
         itemView.tv_artist.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
      }
      itemView.iv_more.setOnClickListener {
         if (listener != null) {
            listener!!.onPartClick(data)
         }
      }

      if (data.isCp) {
         itemView.tv_title.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
         itemView.tv_artist.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
      }

      if (data.type == Constants.LOCAL) {
         itemView.iv_resource.visibility = View.GONE
      } else {
         itemView.iv_resource.visibility = View.VISIBLE
         when {
            data.type == Constants.BAIDU -> {   // 百度音乐
               itemView.iv_resource.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.baidu))
            }
            data.type == Constants.NETEASE -> { // 网易云音乐
               itemView.iv_resource.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.netease))
            }
            data.type == Constants.QQ -> {      // qq 音乐
               itemView.iv_resource.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.qq))
            }
            data.type == Constants.XIAMI -> {
               itemView.iv_resource.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.xiami))
            }
         }
         if (data.coverUri != null) {
            CoverLoader.loadImageView(mContext, data.coverUri!!, itemView.iv_cover)
         }
         if (data.coverUri.isNullOrEmpty()) {
            // 加载歌曲专辑图
            data.title?.let {
//               //加载歌曲专辑图
//               item.title?.let {
//                  MusicApi.getMusicAlbumPic(item.title.toString(), success = {
//                     item.coverUri = it
//                     CoverLoader.loadImageView(mContext, it, holder.getView(R.id.iv_cover))
//                  })
//               }
            }
         }
         if (data.isCp) {
            itemView.setOnClickListener {
               ToastUtils.showToast(mContext, "歌曲无法播放")
            }
         }
      }
   }

   @Subscribe(threadMode = ThreadMode.MAIN)
   fun updateUserInfo(event: MetaChangedEvent) {
      notifyDataSetChanged()
   }

   private var listener : OnItemPartClickListener? = null

   interface OnItemPartClickListener {
      fun onPartClick(music: Music)
   }

   fun setOnItemPartClickListener(listener : OnItemPartClickListener) {
      this.listener = listener
   }

   override fun getAdapterLayoutId(viewType: Int): Int = R.layout.activity_music_item

   override fun onViewAttachedToWindow(holder: BaseRecyclerHolder) {
      super.onViewAttachedToWindow(holder)
      if (!EventBus.getDefault().isRegistered(this)) {
         EventBus.getDefault().register(this)
      }
   }

   override fun onViewDetachedFromWindow(holder: BaseRecyclerHolder) {
      super.onViewDetachedFromWindow(holder)
      if (EventBus.getDefault().isRegistered(this)) {
         EventBus.getDefault().unregister(this)
      }
   }
}































