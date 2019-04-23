// IOnPlayListChangedListener.aidl
package com.jiwenjie.cocomusic.aidl;
import com.jiwenjie.cocomusic.aidl.Song;

// Declare any non-default types here with import statements

interface IOnPlayListChangedListener {
    void onPlayListChange(in Song current, int index, int id);
}
