package com.supcoder.curtain

import com.supcoder.curtain.config.CurtainType

/**
 * @author lihc15
 * @date 2020/8/12
 */
interface ICurtainView {


    /**
     * 设置窗帘类型
     */
    fun setType(type: CurtainType)

    /**
     * 设置动画执行的时长
     */
    fun setAnimDuration(duration: Long)

    /**
     * 设置窗帘进度(执行动画)
     */
    fun setProgress(progress: Int)

    /**
     * 设置进度（不执行动画）
     */
    fun setProgressImmediately(progress: Int)
}