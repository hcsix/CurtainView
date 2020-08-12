package com.supcoder.curtain

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator


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



    private var animDuration = DefaultVal.ANIM_DURATION

    private var targetProgress = DefaultVal.DEFAULT_PROGRESS

    private var curProgress = 0


    private var animator: ValueAnimator? = null


    var onProgressChangeListener: OnProgressChangeListener? = null


    private val animatorListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val value = animation.animatedValue as Int
        curProgress = value
        onProgressChangeListener?.onProgressChanged(curProgress)
        Log.e(tag, "onAnimationUpdate: $value")
    }



    override fun setAnimDuration(duration: Long) {
        animDuration = duration
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


    interface OnProgressChangeListener {
        fun onProgressChanged(progress: Int)
    }

}