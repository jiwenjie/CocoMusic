package com.jiwenjie.cocomusic.utils

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Flowable.create
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/01/07
 *  desc:
 *  version:1.0
 */
object RxJavaUtils {

   /**
    * 统一线程处理
    * 发布事件io线程，接收事件主线程
    */
   @JvmStatic
   fun <T> applyObservableAsync(): ObservableTransformer<T, T> {
      return ObservableTransformer { upstream ->
         upstream.subscribeOn(Schedulers.io())
                 .unsubscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
      }
   }

   /**
    * 生成Flowable
    *
    * @param t
    * @return Flowable
    */
   @JvmStatic
   fun <T> createFlowable(t: T): Flowable<T> {
      return create({ emitter ->
         try {
            emitter.onNext(t)
            emitter.onComplete()
         } catch (e: Exception) {
            emitter.onError(e)
         }
      }, BackpressureStrategy.BUFFER)
   }

   /**
    * 生成Observable
    *
    * @param t
    */
   @JvmStatic
   fun <T> createObservable(t: T): Observable<T> {
      return Observable.create<T> { emitter ->
         try {
            if (!emitter.isDisposed) {
               emitter.onNext(t)
               emitter.onComplete()
            }
         } catch (e: Exception) {
            emitter.onError(e)
         }
      }
   }
}
