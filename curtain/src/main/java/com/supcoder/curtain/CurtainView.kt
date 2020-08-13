package com.supcoder.curtain

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator


/**
 * 窗帘View
 * @author lhc
 * @date 2020/8/12
 */
class CurtainView : View, ICurtainView {

    val tag = "CurtainView"

    constructor(context: Context?) : super(context) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    }


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }


    /**
     * 动画持续时长
     */
    private var animDuration = DefaultVal.ANIM_DURATION

    /**
     * 目标进度
     */
    private var targetProgress = DefaultVal.DEFAULT_PROGRESS

    /**
     * 当前进度
     */
    private var curProgress = 0

    /**
     * 是否镜像
     */
    private var isMirror = false


    /**
     *  进度变化监听
     */
    var onProgressChangeListener: OnProgressChangeListener? = null


    private var animator: ValueAnimator? = null

    private val animatorListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val value = animation.animatedValue as Int
        curProgress = value
        invalidate()
        onProgressChangeListener?.onProgressChanged(curProgress)
        Log.e(tag, "onAnimationUpdate: $value")
    }


    private var paint: Paint = Paint()


    override fun setIsMirror(isMirror: Boolean) {
        this.isMirror = isMirror
    }


    override fun setAnimDuration(duration: Long) {
        this.animDuration = duration
    }


    override fun setProgress(progress: Int) {
        Log.e(tag, "progress -> $progress")
        targetProgress = progress
        animator?.let {
            it.removeAllUpdateListeners()
            if (it.isRunning) {
                it.cancel()
            }
        }
        animator = ValueAnimator.ofInt(curProgress, targetProgress)
        animator?.let {
            it.duration = animDuration
            it.interpolator = DecelerateInterpolator()
            it.addUpdateListener(animatorListener)
        }
        animator?.start()
    }

    override fun setProgressImmediately(progress: Int) {
        targetProgress = progress
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCurtainSheets(canvas)
    }

    private fun drawCurtainSheets(canvas: Canvas) {
        paint.isAntiAlias = true
        paint.strokeWidth = 1f

        val width = width
        val height = height

        val offsetProgress =
            DefaultVal.MIN_FOLD_WIDTH_RATE * 100 + curProgress * (1 - DefaultVal.MIN_FOLD_WIDTH_RATE)

        val curtainAllSheetWidth = offsetProgress * width / 100f - 1f

        val sheetWith = curtainAllSheetWidth / DefaultVal.SHEET_NUM

        val arcPointY = height - DefaultVal.MAX_ARC_HEIGHT.coerceAtMost(height * 0.1f)

        val path = Path()
        for (i in 0 until DefaultVal.SHEET_NUM) {
            path.reset()
            path.moveTo(i * sheetWith, height.toFloat())
            path.lineTo(i * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, arcPointY)
            path.moveTo((i + 1) * sheetWith, arcPointY)
            path.quadTo(
                (i + 0.5f) * sheetWith,
                height - (height * 0.01f).coerceAtLeast(5f),
                i * sheetWith,
                height.toFloat()
            )

            paint.color = Color.WHITE
            paint.alpha = (255 * 0.8).toInt()
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)

            paint.color = Color.BLACK
            paint.alpha = (255 * 0.1).toInt()
            paint.style = Paint.Style.STROKE
            canvas.drawPath(path, paint)
        }

    }


    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }

}