package com.jiwenjie.cocomusic.ui.adapter

import android.content.Context
import android.os.Build
import android.view.View
import com.jiwenjie.basepart.adapters.BaseRecyclerAdapter
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.bean.Artist
import com.jiwenjie.cocomusic.common.Constants
import com.jiwenjie.cocomusic.utils.CoverLoader
import kotlinx.android.synthetic.main.fragment_artist.view.*

/**
 *  author:stormwenjie
 *  email:Jiwenjie97@gmail.com
 *  time:2019/06/09
 *  desc:main -> localMusic -> artist -> adapter
 */
class ArtistListAdapter(context: Context, beanList: ArrayList<Artist>): BaseRecyclerAdapter<Artist>(context, beanList) {

    override fun convertView(itemView: View, data: Artist, position: Int) {
        itemView.name.text = data.name
        itemView.artist.text = data.musicSize.toString() + "首歌"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.album.transitionName = Constants.TRANSTITION_ALBUM
        }
        CoverLoader.loadImageView(mContext, data.picUrl, itemView.album)
        if (data.picUrl.isNullOrEmpty()) {
            data.name?.let {
//                MusicApi.getMusicAlbumPic(data.name.toString(), success = {
//                    data.picUrl = it
//                    data.save()
//                    CoverLoader.loadImageView(mContext, it, helper.getView(R.id.album))
//                })
            }
        }
    }

    override fun getAdapterLayoutId(viewType: Int): Int = R.layout.fragment_artist
}