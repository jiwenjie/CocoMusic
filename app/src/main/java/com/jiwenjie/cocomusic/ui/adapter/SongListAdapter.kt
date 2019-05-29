package com.jiwenjie.cocomusic.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import com.jiwenjie.basepart.adapters.BaseRecyclerAdapter
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.bean.MusicListBean
import com.jiwenjie.cocomusic.widget.XCRoundImageView
import kotlinx.android.synthetic.main.activity_songlist_item.view.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/20
 *  desc:歌单列表适配器
 *  version:1.0
 */
class SongListAdapter(context: Context, beanList: ArrayList<MusicListBean>) :
      BaseRecyclerAdapter<MusicListBean>(context, beanList) {

   override fun convertView(itemView: View, data: MusicListBean, position: Int) {
      itemView.listItemAlbum.setType(XCRoundImageView.DEFAULT_ROUND_BORDER_RADIUS)
      itemView.listItemAlbum.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.default_album))   // 自己先写假数据，都是默认的背景

      itemView.musicListNameText.text = data.name
      itemView.otherMessageText.text = String.format("共有%d首歌, 已下载%d首", data.totalSize, data.downloadSize)

      itemView.editMusicListImg.setOnClickListener {
         if (listener != null) {
            listener?.onMenuClick(data)
         }
      }
   }

   private var listener: OnRightMenuClickListener? = null

   fun setOnRightMenuClickListener(listener: OnRightMenuClickListener) {
      this.listener = listener
   }

   interface OnRightMenuClickListener {
      fun onMenuClick(bean: MusicListBean)
   }

   override fun getAdapterLayoutId(viewType: Int): Int = R.layout.activity_songlist_item
}