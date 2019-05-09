package com.jiwenjie.cocomusic.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.jiwenjie.cocomusic.R
import com.jiwenjie.cocomusic.play.playservice.PlayManager.isPlaying

/**
 *  author:Jiwenjie
 *  email:278630464@qq.com
 *  time:2019/05/04
 *  desc:
 *  version:1.0
 */
class PlayPauseView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

   init {
      init(context, attrs)
   }

   private var mWidth: Int = 0 //View宽度
   private var mHeight: Int = 0 //View高度
   private var mPaint: Paint? = null
   private var mRingPaint: Paint? = null //圆弧
   private var mLeftPath: Path? = null //暂停时左侧竖条Path
   private var mRightPath: Path? = null //暂停时右侧竖条Path
   private var mBorderWidth: Float = 0.toFloat() //两个暂停竖条中间的空隙,默认为两侧竖条的宽度
   private var mGapWidth: Float = 0.toFloat() //两个暂停竖条中间的空隙,默认为两侧竖条的宽度
   private var mProgress: Float = 0.toFloat() //动画Progress
   private var mRect: Rect? = null
   private var mRingRect: RectF? = null
   private var isPlaying = false
   private var isLoading: Boolean = false
   private var startAngle: Float = 0.toFloat()
   private var sweepAngle: Float = 0.toFloat()
   private var mRectWidth: Float = 0.toFloat()  //圆内矩形宽度
   private var mRectHeight: Float = 0.toFloat() //圆内矩形高度
   private var mRectLT: Int = 0  //矩形左侧上侧坐标
   private var mRadius: Int = 0  //圆的半径
   private var mBgColor = Color.WHITE
   private var mBtnColor = Color.BLACK
   private var mDirection = Direction.POSITIVE.value
   private var mPadding: Float = 0.toFloat()
   private var mAnimDuration = 200//动画时间

   private fun init(context: Context, attrs: AttributeSet?) {
      val ta = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseView)
      mBgColor = ta.getColor(R.styleable.PlayPauseView_bg_color, Color.WHITE)
//      mBtnColor = ta.getColor(R.styleable.PlayPauseView_btn_color, Color.BLACK)
      mBtnColor = ta.getColor(R.styleable.PlayPauseView_btn_color, ContextCompat.getColor(context, R.color.colorPrimary))
      mGapWidth = ta.getFloat(R.styleable.PlayPauseView_gap_width, 0f)
      mBorderWidth = ta.getFloat(R.styleable.PlayPauseView_border_width, 20f)
      mDirection = ta.getInt(R.styleable.PlayPauseView_anim_direction, Direction.POSITIVE.value)
      mPadding = ta.getFloat(R.styleable.PlayPauseView_space_padding, 0f)
      mAnimDuration = ta.getInt(R.styleable.PlayPauseView_anim_duration, 200)
      ta.recycle()

      mPaint = Paint()
      mPaint!!.isAntiAlias = true
      mPaint!!.strokeCap = Paint.Cap.ROUND
      mPaint!!.strokeJoin = Paint.Join.ROUND
      mPaint!!.style = Paint.Style.FILL
      mRingPaint = Paint()
      mRingPaint!!.isAntiAlias = true
      mRingPaint!!.color = Color.parseColor("#e91e63")
      mRingPaint!!.strokeWidth = mBorderWidth
      mRingPaint!!.style = Paint.Style.STROKE
      mRingPaint!!.strokeCap = Paint.Cap.ROUND
      mRingPaint!!.strokeJoin = Paint.Join.ROUND

      mLeftPath = Path()
      mRightPath = Path()
      mRect = Rect()
      mRingRect = RectF()
   }

   override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      mWidth = View.MeasureSpec.getSize(widthMeasureSpec)
      mHeight = View.MeasureSpec.getSize(heightMeasureSpec)
      val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
      val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
      when (widthMode) {
         View.MeasureSpec.EXACTLY -> {
            mHeight = Math.min(mWidth, mHeight)
            mWidth = mHeight
            setMeasuredDimension(mWidth, mHeight)
         }
         View.MeasureSpec.AT_MOST -> {
            val density = getResources().getDisplayMetrics().density
            mHeight = (50 * density).toInt()
            mWidth = mHeight //默认50dp
            setMeasuredDimension(mWidth, mHeight)
         }
         View.MeasureSpec.UNSPECIFIED -> {
         }
      }
   }

   override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      super.onSizeChanged(w, h, oldw, oldh)
      mHeight = w
      mWidth = mHeight
      initValue()
   }

   private fun initValue() {
      //        int rectLT = (int) (mWidth / 2 - radius / Math.sqrt(2));
      //        int rectRB = (int) (mWidth / 2 + radius / Math.sqrt(2));
      mRadius = mWidth / 2
      /* if (getPadding() > mRadius / Math.sqrt(2) || mPadding < 0) {
         *//*throw new IllegalArgumentException("The value of your padding is too large. " +
                    "The value must not be greater than " + (int) (mRadius / Math.sqrt(2)));*//*
        }*/
      mPadding = if (getSpacePadding() == 0f) mRadius / 3f else getSpacePadding()
      if (getSpacePadding() > mRadius / Math.sqrt(2.0) || mPadding < 0) {
         mPadding = mRadius / 3f //默认值
      }
      val space = (mRadius / Math.sqrt(2.0) - mPadding).toFloat() //矩形宽高的一半
      mRectLT = (mRadius - space).toInt()
      val rectRB = (mRadius + space).toInt()
      mRect!!.top = mRectLT
      mRect!!.bottom = rectRB
      mRect!!.left = mRectLT
      mRect!!.right = rectRB
      mRingRect!!.top = mRadius - space * 2
      mRingRect!!.bottom = mRadius + space * 2
      mRingRect!!.left = mRadius - space * 2
      mRingRect!!.right = mRadius + space * 2
      //        mRectWidth = mRect.width();
      //        mRectHeight = mRect.height();
      mRectWidth = 2 * space + 2 //改为float类型，否则动画有抖动。并增加一像素防止三角形之间有缝隙
      mRectHeight = 2 * space + 2
      mGapWidth = if (getGapWidth() != 0f) getGapWidth() else mRectWidth / 3
      mProgress = (if (isPlaying) 0 else 1).toFloat()
      mAnimDuration = if (getAnimDuration() < 0) 200 else getAnimDuration()
      startAngle = -90f
      sweepAngle = 120f
      mRingPaint!!.strokeWidth = space / 2
   }

   override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)

      mLeftPath!!.rewind()
      mRightPath!!.rewind()

      //        mPaint.setStrokeWidth(1);
      //        mPaint.setStyle(Paint.Style.STROKE);
      mPaint!!.color = mBgColor
      canvas.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), mRadius.toFloat(), mPaint!!)
      //        canvas.drawRect(mRect, mPaint);
      if (isLoading) {
         canvas.drawArc(mRingRect!!, startAngle, sweepAngle, false, mRingPaint!!) //
      }

      val distance = mGapWidth * (1 - mProgress)  //暂停时左右两边矩形距离
      val barWidth = mRectWidth / 2 - distance / 2     //一个矩形的宽度
      val leftLeftTop = barWidth * mProgress       //左边矩形左上角

      val rightLeftTop = barWidth + distance       //右边矩形左上角
      val rightRightTop = 2 * barWidth + distance  //右边矩形右上角
      val rightRightBottom = rightRightTop - barWidth * mProgress //右边矩形右下角

      mPaint!!.color = mBtnColor

      if (mDirection == Direction.NEGATIVE.value) {
         mLeftPath!!.moveTo(mRectLT.toFloat(), mRectLT.toFloat())
         mLeftPath!!.lineTo(leftLeftTop + mRectLT, mRectHeight + mRectLT)
         mLeftPath!!.lineTo(barWidth + mRectLT, mRectHeight + mRectLT)
         mLeftPath!!.lineTo(barWidth + mRectLT, mRectLT.toFloat())
         mLeftPath!!.close()

         mRightPath!!.moveTo(rightLeftTop + mRectLT, mRectLT.toFloat())
         mRightPath!!.lineTo(rightLeftTop + mRectLT, mRectHeight + mRectLT)
         mRightPath!!.lineTo(rightRightBottom + mRectLT, mRectHeight + mRectLT)
         mRightPath!!.lineTo(rightRightTop + mRectLT, mRectLT.toFloat())
         mRightPath!!.close()
      } else {
         mLeftPath!!.moveTo(leftLeftTop + mRectLT, mRectLT.toFloat())
         mLeftPath!!.lineTo(mRectLT.toFloat(), mRectHeight + mRectLT)
         mLeftPath!!.lineTo(barWidth + mRectLT, mRectHeight + mRectLT)
         mLeftPath!!.lineTo(barWidth + mRectLT, mRectLT.toFloat())
         mLeftPath!!.close()

         mRightPath!!.moveTo(rightLeftTop + mRectLT, mRectLT.toFloat())
         mRightPath!!.lineTo(rightLeftTop + mRectLT, mRectHeight + mRectLT)
         mRightPath!!.lineTo(rightLeftTop + mRectLT.toFloat() + barWidth, mRectHeight + mRectLT)
         mRightPath!!.lineTo(rightRightBottom + mRectLT, mRectLT.toFloat())
         mRightPath!!.close()
      }

      canvas.save()

      canvas.translate(mRectHeight / 8f * mProgress, 0f)

      val progress = if (isPlaying) 1 - mProgress else mProgress
      val corner = if (mDirection == Direction.NEGATIVE.value) -90 else 90
      val rotation = if (isPlaying) corner * (1 + progress) else corner * progress
      canvas.rotate(rotation, mWidth / 2f, mHeight / 2f)

      canvas.drawPath(mLeftPath!!, mPaint!!)
      canvas.drawPath(mRightPath!!, mPaint!!)

      canvas.restore()
   }


   /**
    * 显示Loading 动画
    *
    * @return
    */
   fun getLoadingAnim(): ValueAnimator {
      val valueAnimator = ValueAnimator.ofFloat(0f, 360f)
      valueAnimator.repeatCount = ObjectAnimator.INFINITE
      valueAnimator.repeatMode = ObjectAnimator.RESTART
      valueAnimator.duration = 2000
      valueAnimator.addUpdateListener { animation ->
         sweepAngle = animation.animatedValue as Float
         if (sweepAngle >= 90) {
            startAngle++
            if (startAngle >= 360) {
               startAngle = 0f
            }
         }
         invalidate()
      }
      return valueAnimator
   }


   fun getPlayPauseAnim(): ValueAnimator {
      val valueAnimator = ValueAnimator.ofFloat(if (isPlaying) 1F else 0F, if (isPlaying) 0F else 1F)
      valueAnimator.duration = mAnimDuration.toLong()
      valueAnimator.addUpdateListener { animation ->
         mProgress = animation.animatedValue as Float
         invalidate()
      }
      return valueAnimator
   }

   /**
    * 开始Loading
    */
   fun startLoading() {
      if (getLoadingAnim() != null) {
         getLoadingAnim().cancel()
      }
      getLoadingAnim().start()
   }

   /**
    * 停止Loading
    */
   fun stopLoading() {
      if (getLoadingAnim() != null) {
         getLoadingAnim().cancel()
      }
   }


   fun play() {
      if (getPlayPauseAnim() != null) {
         getPlayPauseAnim().cancel()
      }
      setPlaying(true)
      getPlayPauseAnim().start()
   }

   fun pause() {
      if (getPlayPauseAnim() != null) {
         getPlayPauseAnim().cancel()
      }
      setPlaying(false)
      getPlayPauseAnim().start()
   }

   private var mPlayPauseListener: PlayPauseListener? = null

   fun setPlayPauseListener(playPauseListener: PlayPauseListener) {
      mPlayPauseListener = playPauseListener
      setOnClickListener {
         if (isPlaying()) {
            pause()
            if (null != mPlayPauseListener) {
               mPlayPauseListener!!.pause()
            }
         } else {
            play()
            if (null != mPlayPauseListener) {
               mPlayPauseListener!!.play()
            }
         }
      }
   }

   interface PlayPauseListener {
      fun play()

      fun pause()
   }

   /* ------------下方是参数------------- */

   fun getPlaing(): Boolean {
      return isPlaying
   }

   fun setPlaying(playing: Boolean) {
      isPlaying = playing
   }

   fun setGapWidth(gapWidth: Int) {
      mGapWidth = gapWidth.toFloat()
   }

   fun getGapWidth(): Float {
      return mGapWidth
   }

   fun getBgColor(): Int {
      return mBgColor
   }

   fun getBtnColor(): Int {
      return mBtnColor
   }

   fun getDirection(): Int {
      return mDirection
   }

   fun setBgColor(bgColor: Int) {
      mBgColor = bgColor
   }

   fun setBtnColor(btnColor: Int) {
      mBtnColor = btnColor
      invalidate()
   }

   fun setLoading(loading: Boolean) {
      isLoading = loading
      if (isLoading) {
         startLoading()
      } else {
         stopLoading()
      }
      invalidate()
   }

   fun setDirection(direction: Direction) {
      mDirection = direction.value
   }

   fun getSpacePadding(): Float {
      return mPadding
   }

   fun setSpacePadding(padding: Float) {
      mPadding = padding
   }

   fun getAnimDuration(): Int {
      return mAnimDuration
   }

   fun setAnimDuration(animDuration: Int) {
      mAnimDuration = animDuration
   }

   enum class Direction constructor(//逆时针
           internal var value: Int) {
      POSITIVE(1), //顺时针
      NEGATIVE(2)
   }
}