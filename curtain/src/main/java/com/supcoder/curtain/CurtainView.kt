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
import com.supcoder.curtain.bridge.ICurtainView
import com.supcoder.curtain.bridge.OnProgressChangeListener
import com.supcoder.curtain.config.CurtainType


/**
 * 窗帘View
 * @author lhc
 * @date 2020/8/12
 */
class CurtainView : View, ICurtainView {

    val tag = "CurtainView"

    /**
     * 动画持续时长
     */
    private var animDuration = DefaultVal.ANIM_DURATION

    /**
     * 窗帘类型
     */
    private var type = CurtainType.LEFT

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        val attr = context.obtainStyledAttributes(attrs, R.styleable.CurtainView, defStyleAttr, 0)
        val typeEnum = attr.getInt(R.styleable.CurtainView_curtainType, 0)

        type = CurtainType.parse(typeEnum)

        animDuration = attr.getInt(R.styleable.CurtainView_animDuration, DefaultVal.ANIM_DURATION)
        attr.recycle()
    }


    /**
     * 目标进度
     */
    private var targetProgress = DefaultVal.DEFAULT_PROGRESS

    /**
     * 当前进度
     */
    private var curProgress = 0


    /**
     *  进度变化监听
     */
    private var onProgressChangeListener: OnProgressChangeListener? = null


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


    override fun setType(type: CurtainType): ICurtainView {
        this.type = type
        return this
    }

    override fun setAnimDuration(duration: Int): ICurtainView {
        this.animDuration = duration
        return this
    }

    override fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener): ICurtainView {
        this.onProgressChangeListener = onProgressChangeListener
        return this
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
            it.duration = animDuration.toLong()
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

        val curtainAllSheetWidth = offsetProgress * width / 100f

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

        val curtainAllSheetWidth = offsetProgress * width / 100f

        val sheetWith = curtainAllSheetWidth / (DefaultVal.SHEET_NUM * 2)

        val arcPointY = height - DefaultVal.MAX_ARC_HEIGHT.coerceAtMost(height * 0.1f)

        val path = Path()
        for (i in 0 until DefaultVal.SHEET_NUM) {
            //绘制左侧窗帘
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

            //绘制右侧窗帘
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


}