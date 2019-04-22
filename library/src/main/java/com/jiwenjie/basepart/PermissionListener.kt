package com.jiwenjie.basepart

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/04/04
 *  desc:
 *  version:1.0
 */
interface PermissionListener {
    fun onGranted()
    fun onDenied(deniedPermissions: List<String>)
}