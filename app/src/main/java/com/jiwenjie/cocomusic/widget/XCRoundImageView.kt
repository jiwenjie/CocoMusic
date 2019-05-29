package com.jiwenjie.cocomusic.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import java.lang.ref.WeakReference
import android.graphics.Bitmap

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/19
 *  desc:
 *  version:1.0
 */
class XCRoundImageView(context: Context, attributeSet: AttributeSet?) : ImageView(context, attributeSet) {

   // 数据定义
   private var mPaint: Paint? = null
   private val mXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
   private var mMaskBitmap: Bitmap? = null
   private var mRoundBorderRadius = 0        // 圆角大小
   private var mType = -1                 // 类型：圆形、圆角或椭圆

   private var mBufferBitmap: WeakReference<Bitmap>? = null       // 使用缓存技术，避免每次都执行onDraw

   companion object {
      /**
       * 圆
       */
      const val TYPE_CIRCLE = 1
      /**
       * 四角圆矩形
       */
      const val TYPE_ROUND = 2
      /**
       * 椭圆形
       */
      const val TYPE_OVAL = 3
      var DEFAULT_ROUND_BORDER_RADIUS = 6         // 默认圆角大小
   }

   init {
      mPaint = Paint()
      mPaint?.isAntiAlias = true       // 设置消除锯齿
      mType = TYPE_ROUND
      mRoundBorderRadius = DEFAULT_ROUND_BORDER_RADIUS
   }

   override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      // 如果类型是圆形，则强制设置view的宽高一致，取宽高的较小值
      if (mType == TYPE_CIRCLE) {
         val width = Math.min(measuredWidth, measuredHeight)
         setMeasuredDimension(width, width)
      }
   }

   @SuppressLint("DrawAllocation")
   override fun onDraw(canvas: Canvas?) {
      super.onDraw(canvas)
      // 从缓存中取出bitmap
      var bmp = if (mBufferBitmap == null) null else mBufferBitmap?.get()
      if (bmp == null || bmp.isRecycled) {
         // 如果没有缓存存在的情况
         // 获取drawable
         if (null != drawable) {
            // 获取drawable的宽高
            val dwidth = drawable.intrinsicWidth
            val dheight = drawable.intrinsicHeight
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var scale = 1.0f
            // 创建画布
            val drawCanvas = Canvas(bmp!!)
            // 按照bitmap的宽高，以及view的宽高，计算缩放比例；因为设置的src宽高
            // 比例可能和imageview的宽高比例不同，这里我们不希望图片失真；

            if (mType == TYPE_CIRCLE) {// 如果是圆形
               scale = width * 1.0F / Math.min(dwidth, dheight)
            } else if (mType == TYPE_ROUND || mType == TYPE_OVAL) {// 如果是圆角矩形或椭圆
               // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；
               // 缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
               scale = Math.max(width * 1.0f / dwidth, height * 1.0f / dheight)
            }
            // 根据缩放比例，设置bounds，即相当于做缩放图片
            drawable.setBounds(0, 0, (scale * dwidth).toInt(), (scale * dheight).toInt())
            drawable.draw(drawCanvas);
            // 获取bitmap，即圆形、圆角或椭圆的bitmap
            if (mMaskBitmap == null || mMaskBitmap!!.isRecycled) {
               mMaskBitmap = getDrawBitmap()
            }
            // 为paint设置Xfermode 渲染模式
            mPaint?.reset()
            mPaint?.isFilterBitmap = false
            mPaint?.xfermode = mXfermode
            // 绘制不同形状
            if (mMaskBitmap != null) {
               drawCanvas.drawBitmap(mMaskBitmap!!, 0f, 0f, mPaint)
            }
            mPaint?.xfermode = null

            // 将准备好的bitmap绘制出来
            canvas?.drawBitmap(bmp, 0f, 0f, null)
            // bitmap缓存起来，避免每次调用onDraw，分配内存
            mBufferBitmap = WeakReference(bmp)
         }

      } else {
         // 如果缓存还存在的情况
         mPaint?.xfermode = null
         canvas?.drawBitmap(bmp, 0.0f, 0.0f, mPaint)
         return
      }
   }

   /**
    * 绘制不同的图形Bitmap
    */
   private fun getDrawBitmap(): Bitmap? {
      var bitmap: Bitmap? = null
      try {
         bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
         val canvas = Canvas(bitmap!!)
         val paint = Paint(Paint.ANTI_ALIAS_FLAG)
         paint.color = Color.BLACK

         if (mType == TYPE_CIRCLE) {// 绘制圆形
            canvas.drawCircle((width / 2).toFloat(), (width / 2).toFloat(), (width / 2).toFloat(), paint)
         } else if (mType == TYPE_ROUND) {// 绘制圆角矩形
            canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), mRoundBorderRadius.toFloat(), mRoundBorderRadius.toFloat(), paint)
         } else if (mType == TYPE_OVAL) {
            // 绘制椭圆
            canvas.drawOval(RectF(0f, 0f, width.toFloat(), height.toFloat()), mPaint)
         }
      } catch (e: Throwable) {
      }

      return bitmap
   }

   override fun invalidate() {
      mBufferBitmap = null
      if (mMaskBitmap != null) {
         mMaskBitmap!!.recycle()
         mMaskBitmap = null
      }
      super.invalidate()
   }

   fun getRoundBorderRadius(): Int {
      return mRoundBorderRadius
   }

   fun setRoundBorderRadius(mRoundBorderRadius: Int) {
      if (this.mRoundBorderRadius != mRoundBorderRadius) {
         this.mRoundBorderRadius = mRoundBorderRadius
         invalidate()
      }
   }

   fun getType(): Int {
      return this.mType
   }

   fun setType(mType: Int) {
      if (this.mType != mType) {
         this.mType = mType
         invalidate()
      }
   }
}