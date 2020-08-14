package com.supcoder.curtain

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.supcoder.curtain.config.CurtainType


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
    private var type = CurtainType.LEFT


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


    private var sheetPaint = Paint()
    private var borderPaint = Paint()


    override fun setType( type: CurtainType) {
        this.type = type
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


        sheetPaint.apply {
            isAntiAlias = true
            color = Color.WHITE
            alpha = (255 * 0.6).toInt()
            style = Paint.Style.FILL
        }

        borderPaint.apply {
            isAntiAlias = true
            strokeWidth = DefaultVal.SHEET_BORDER_WIDTH
            color = Color.GRAY
            alpha = (255 * 0.2).toInt()
            style = Paint.Style.STROKE
        }


        when (type) {
            CurtainType.LEFT -> {
                drawSingleCurtain(canvas, true)
            }
            CurtainType.RIGHT -> {
                drawSingleCurtain(canvas, false)
            }
            CurtainType.BOTH -> {
                drawBothCurtain(canvas)
            }
        }

    }


    /**
     * 绘制单侧窗帘
     * @param canvas 画布
     * @param isLeft 是否是左侧画布
     */
    private fun drawSingleCurtain(canvas: Canvas, isLeft: Boolean) {
        //如果是左侧窗帘则镜像翻转canvas
        if (!isLeft) {
            canvas.scale(-1f, 1f, width / 2f, height / 2f)
        }

        val width = width
        val height = height - 2

        val offsetProgress =
            DefaultVal.MIN_FOLD_WIDTH_RATE * 100 + curProgress * (1 - DefaultVal.MIN_FOLD_WIDTH_RATE)

        val curtainAllSheetWidth = offsetProgress * width / 100f - DefaultVal.SHEET_BORDER_WIDTH

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
                height - (height * 0.01f).coerceAtLeast(4f),
                i * sheetWith,
                height.toFloat()
            )
            canvas.drawPath(path, sheetPaint)
            canvas.drawPath(path, borderPaint)
        }


        for (i in 0 until DefaultVal.SHEET_NUM) {
            path.reset()
            path.moveTo(i * sheetWith, height.toFloat())
            path.lineTo(i * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, arcPointY)
            path.moveTo((i + 1) * sheetWith, arcPointY)
            path.quadTo(
                (i + 0.5f) * sheetWith,
                height - (height * 0.01f).coerceAtLeast(4f),
                i * sheetWith,
                height.toFloat()
            )
            canvas.drawPath(path, sheetPaint)
            canvas.drawPath(path, borderPaint)
        }
    }

    /**
     * 绘制双叶窗帘
     */
    private fun drawBothCurtain(canvas: Canvas) {
        val width = width
        val height = height - 2

        val offsetProgress =
            DefaultVal.MIN_FOLD_WIDTH_RATE * 100 + curProgress * (1 - DefaultVal.MIN_FOLD_WIDTH_RATE)

        val curtainAllSheetWidth = offsetProgress * width / 100f - DefaultVal.SHEET_BORDER_WIDTH * 2

        val sheetWith = curtainAllSheetWidth / (DefaultVal.SHEET_NUM * 2)

        val arcPointY = height - DefaultVal.MAX_ARC_HEIGHT.coerceAtMost(height * 0.1f)

        val path = Path()
        for (i in 0 until DefaultVal.SHEET_NUM) {
            path.reset()
            path.moveTo(width - i * sheetWith, height.toFloat())
            path.lineTo(width - i * sheetWith, 0f)
            path.lineTo(width - (i + 1) * sheetWith, 0f)
            path.lineTo(width - (i + 1) * sheetWith, arcPointY)
            path.moveTo(width - (i + 1) * sheetWith, arcPointY)
            path.quadTo(
                width - (i + 0.5f) * sheetWith,
                height - (height * 0.01f).coerceAtLeast(4f),
                width - i * sheetWith,
                height.toFloat()
            )
            canvas.drawPath(path, sheetPaint)
            canvas.drawPath(path, borderPaint)
        }

        for (i in 0 until DefaultVal.SHEET_NUM) {
            path.reset()
            path.moveTo(i * sheetWith, height.toFloat())
            path.lineTo(i * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, 0f)
            path.lineTo((i + 1) * sheetWith, arcPointY)
            path.moveTo((i + 1) * sheetWith, arcPointY)
            path.quadTo(
                (i + 0.5f) * sheetWith,
                height - (height * 0.01f).coerceAtLeast(4f),
                i * sheetWith,
                height.toFloat()
            )
            canvas.drawPath(path, sheetPaint)
            canvas.drawPath(path, borderPaint)
        }

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.let {
            it.removeAllUpdateListeners()
            if (it.isRunning) {
                it.cancel()
            }
        }
    }


    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }


}