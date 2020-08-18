package com.supcoder.curtain

import android.animation.Animator
import android.animation.Animator.AnimatorListener
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
import com.supcoder.curtain.bridge.OnAnimEndListener
import com.supcoder.curtain.bridge.OnProgressChangeListener
import com.supcoder.curtain.config.CurtainType


/**
 * 窗帘View
 * @author lhc
 * @date 2020/8/12
 */
class CurtainView : View, ICurtainView {


    /**
     * 动画持续时长
     */
    private var animDuration = DefaultVal.ANIM_DURATION

    /**
     * 窗帘类型
     */
    private var type = CurtainType.LEFT


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

    /**
     * 动画结束监听
     */
    private var onAnimEndListener: OnAnimEndListener? = null


    private var animator: ValueAnimator? = null

    private val animUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val value = animation.animatedValue as Int
        curProgress = value
        invalidate()
        onProgressChangeListener?.onProgressChanged(curProgress)
    }

    private val animStateListener = object : AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            onAnimEndListener?.onAnimEnd()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }

    }


    private var sheetPaint = Paint()

    private var borderPaint = Paint()

    private var sheetColor = DefaultVal.SHEET_COLOR

    private var sheetAlpha = DefaultVal.SHEET_ALPHA


    private var borderColor = DefaultVal.BORDER_COLOR

    private var borderAlpha = DefaultVal.BORDER_ALPHA


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

    override fun setOnAnimEndListener(onAnimEndListener: OnAnimEndListener): ICurtainView {
        this.onAnimEndListener = onAnimEndListener
        return this
    }

    override fun setSheetColor(color: Int, alpha: Int): ICurtainView {
        this.sheetColor = color
        this.sheetAlpha = alpha
        invalidate()
        return this
    }

    override fun setBorderColor(color: Int, alpha: Int): ICurtainView {
        this.borderColor = color
        this.borderAlpha = alpha
        invalidate()
        return this
    }




    override fun setProgress(progress: Int) {
        targetProgress = progress
        animator?.let {
            it.removeAllUpdateListeners()
            it.removeAllListeners()
            if (it.isRunning) {
                it.cancel()
            }
        }
        animator = ValueAnimator.ofInt(curProgress, targetProgress)
        animator?.let {
            it.duration = animDuration.toLong()
            it.interpolator = DecelerateInterpolator()
            it.addUpdateListener(animUpdateListener)
            it.addListener(animStateListener)
        }
        animator?.start()
    }

    override fun setProgressImmediately(progress: Int) {
        targetProgress = progress
        curProgress = progress
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        sheetPaint.apply {
            isAntiAlias = true
            color = sheetColor
            alpha = sheetAlpha
            style = Paint.Style.FILL
        }

        borderPaint.apply {
            isAntiAlias = true
            strokeWidth = DefaultVal.SHEET_BORDER_WIDTH
            color = borderColor
            alpha = borderAlpha
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