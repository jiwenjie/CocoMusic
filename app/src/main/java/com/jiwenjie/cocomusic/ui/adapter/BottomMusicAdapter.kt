package com.jiwenjie.cocomusic.ui.adapter

import android.content.Context
import android.view.View
import com.jiwenjie.basepart.adapters.BaseRecyclerAdapter
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.utils.CommonUtils
import com.jiwenjie.cocomusic.utils.CoverLoader
import kotlinx.android.synthetic.main.item_bottom_music.view.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/09
 *  desc:本地歌曲item
 *  version:1.0
 */
class BottomMusicAdapter(context: Context, beanList: ArrayList<Music>) : BaseRecyclerAdapter<Music>(context, beanList) {

    override fun getAdapterLayoutId(viewType: Int): Int = R.layout.item_bottom_music

    override fun convertView(itemView: View, data: Music, position: Int) {
        CoverLoader.loadImageView(mContext, data.coverUri!!, itemView.iv_cover)

        itemView.tv_title.text = CommonUtils.getTitle(data.title)
        // 设置歌手专辑名称
        itemView.tv_artist.text = CommonUtils.getArtistAndAlbum(data.artist!!, data.album!!)

        if (data.coverUri != null) {
            CoverLoader.loadImageView(mContext, data.coverUri!!, R.drawable.default_cover, itemView.iv_cover)
        }
        if (data.coverUri.isNullOrEmpty()) {
            // 加载歌曲专辑图
            data.title?.let {
                // 加载网络歌曲的专辑图
//                MusicApi.getMusicAlbumPic(item.title.toString(), success = {
//                    item.coverUri = it
//                    CoverLoader.loadImageView(mContext, it, R.drawable.default_cover, holder.getView(R.id.iv_cover))
//                })
            }
        }
    }
}