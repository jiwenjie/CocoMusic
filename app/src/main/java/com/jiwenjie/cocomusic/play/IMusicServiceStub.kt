package com.jiwenjie.cocomusic.play

import com.jiwenjie.cocomusic.aidl.IMusicService
import com.jiwenjie.cocomusic.aidl.Music
import com.jiwenjie.cocomusic.play.playservice.MusicPlayerService
import java.lang.ref.WeakReference

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:
 *  version:1.0
 */
class IMusicServiceStub(service: MusicPlayerService) : IMusicService.Stub() {

    private var mService: WeakReference<MusicPlayerService>? = null

    init {
        mService = WeakReference(service)
    }

    override fun nextPlay(music: Music?) {
        mService!!.get()!!.nextPlay(music!!)
    }

    override fun playMusic(music: Music?) {
        mService!!.get()!!.play(music)
    }

    override fun playPlaylist(playlist: MutableList<Music>?, id: Int, pid: String?) {
        mService!!.get()!!.play(playlist as ArrayList<Music>, id, pid!!)
    }

    override fun play(id: Int) {
        mService!!.get()!!.playMusic(id)
    }

    override fun playPause() {
        mService!!.get()!!.playPause()
    }

    override fun pause() {
        mService!!.get()!!.pause()
    }

    override fun stop() {
        mService!!.get()!!.stop(true)
    }

    override fun prev() {
        mService!!.get()!!.prev()
    }

    override fun next() {
        mService!!.get()!!.next(false)
    }

    override fun setLoopMode(loopmode: Int) {
    }

    override fun seekTo(ms: Int) {
        mService!!.get()!!.seekTo(ms.toLong(), false)
    }

    override fun position(): Int {
        return mService!!.get()!!.getPlayPosition()
    }

    override fun getDuration(): Int {
        return mService!!.get()!!.getDuration().toInt()
    }

    override fun getCurrentPosition(): Int {
        return mService!!.get()!!.getCurrentPosition().toInt()
    }

    override fun isPlaying(): Boolean {
        return mService!!.get()!!.isPlaying()
    }

    override fun isPause(): Boolean {
        return mService!!.get()!!.isPlaying()
    }

    override fun getSongName(): String? {
        return mService!!.get()!!.getTitle()
    }

    override fun getSongArtist(): String? {
        return mService!!.get()!!.getArtistName()
    }

    override fun getPlayingMusic(): Music? {
        return mService!!.get()!!.getPlayingMusic()
    }

    override fun getPlayList(): List<Music>? {
        return mService!!.get()!!.getPlayQueue()
    }

    override fun removeFromQueue(position: Int) {
        mService!!.get()!!.removeFromQueue(position)
    }

    override fun clearQueue() {
        mService!!.get()!!.clearQueue()
    }

    override fun showDesktopLyric(show: Boolean) {
        mService!!.get()!!.showDesktopLyric(show)
    }

    override fun AudioSessionId(): Int {
        return  mService!!.get()!!.getAudioSessionId()
    }
}