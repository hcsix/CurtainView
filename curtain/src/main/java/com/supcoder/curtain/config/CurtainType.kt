package com.supcoder.curtain.config

import androidx.annotation.IntDef

/**
 * 窗帘类型定义
 * @author lihc
 * @date 2020/8/14
 */
object CurtainType {

    /**
     * 右侧窗帘
     */
    const val RIGHT = 0

    /**
     * 左侧窗帘
     */
    const val LEFT = 1

    /**
     * 左右都有的窗帘
     */
    const val BOTH = 2




    @IntDef(
        BOTH,
        LEFT,
        RIGHT
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class TypeDef

}