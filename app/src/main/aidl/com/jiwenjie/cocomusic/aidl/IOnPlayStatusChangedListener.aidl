// IOnPlayStatusChangedListener.aidl
package com.jiwenjie.cocomusic.aidl;
import com.jiwenjie.cocomusic.aidl.Song;

// Declare any non-default types here with import statements

interface IOnPlayStatusChangedListener {
    // 自定播放时歌曲播放完成时回调
    void playStop(in Song whitch, int index, int status);

    // 自动播放时歌曲开始播放时回调
    void playStart(in Song whitch, int index, int status);
}
