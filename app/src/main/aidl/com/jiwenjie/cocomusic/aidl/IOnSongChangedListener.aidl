// IOnSongChangedListener.aidl
package com.jiwenjie.cocomusic.aidl;
import com.jiwenjie.cocomusic.aidl.Song;

// Declare any non-default types here with import statements

interface IOnSongChangedListener {
    // 改方法运行在线程池中（非 UI 线程）
    void onSongChange(in Song whitch, int index, boolean isNext);
}
