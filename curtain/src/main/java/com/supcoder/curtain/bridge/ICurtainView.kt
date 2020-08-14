package com.supcoder.curtain.bridge

import com.supcoder.curtain.config.CurtainType

/**
 * @author lihc15
 * @date 2020/8/12
 */
interface ICurtainView {

    /******************************** 窗帘属性配置(返回ICurtainView，方便JAVA链式调用) START ********************************/
    /**
     * 设置窗帘类型
     */
    fun setType(type: CurtainType): ICurtainView

    /**
     * 设置动画执行的时长
     */
    fun setAnimDuration(duration: Int): ICurtainView

    /**
     * 设置进度变化监听
     */
    fun setOnProgressChangeListener(onProgressChangeListener: OnProgressChangeListener) :ICurtainView

    /******************************** 窗帘属性配置 END **************************************************************************/


    /**
     * 设置窗帘进度(执行动画)
     */
    fun setProgress(progress: Int)

    /**
     * 设置进度（不执行动画）
     */
    fun setProgressImmediately(progress: Int)
}