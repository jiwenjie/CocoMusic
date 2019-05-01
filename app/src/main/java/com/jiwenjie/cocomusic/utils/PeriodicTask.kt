package com.jiwenjie.cocomusic.utils

import java.util.*

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/01
 *  desc:
 *  version:1.0
 */
class PeriodicTask(task: Task, period: Int) {

   private var progressUpdateTask: TimerTask? = null
   private var task: Task? = null
   private var period: Int = -1
   private var isSchedule = false

   interface Task {
      /**
       * 执行周期任务，注意该方法不在主线程执行，不能在其中访问 UI 控件
       */
      fun execute()
   }

   init {
      this.task = task
      this.period = period
   }

   fun stop() {
      if (!isSchedule()) return

      if (progressUpdateTask != null) {
         progressUpdateTask!!.cancel()
         isSchedule = false
      }
   }

   fun start() {
      if (isSchedule()) return

      val timer = Timer()
      progressUpdateTask = object :TimerTask() {
         override fun run() {
            task!!.execute()
         }
      }
      timer.schedule(progressUpdateTask, 0, period.toLong())
      isSchedule = true
   }

   fun isSchedule(): Boolean {
      return isSchedule
   }

}

























