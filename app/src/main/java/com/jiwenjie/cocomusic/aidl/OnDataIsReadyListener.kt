package com.jiwenjie.cocomusic.aidl

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/23
 *  desc:
 *  version:1.0
 */
abstract class OnDataIsReadyListener : IOnDataIsReadyListener.Stub() {

    /**
     * 再次之前服务端已经能够对监听者分发其他事件回调，而客户端想要与服务端交互（操作服务端），应从这里开始
     */
    abstract override fun dataIsReady()
}