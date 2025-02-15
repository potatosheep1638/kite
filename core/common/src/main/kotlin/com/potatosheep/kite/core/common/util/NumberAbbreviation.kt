package com.potatosheep.kite.core.common.util

fun Int.abbreviate(): String {
    val intString = this.toString()

    return when (intString.length) {
        9 -> {
            "${intString.substring(0, 3)}.${intString.substring(3, 4)}M"
        }
        8 -> {
            "${intString.substring(0, 2)}.${intString.substring(2, 3)}M"
        }
        7 -> {
            "${intString.substring(0, 1)}.${intString.substring(1, 2)}M"
        }
        6 -> {
            "${intString.substring(0, 3)}K"
        }
        5 -> {
            "${intString.substring(0, 2)}K"
        }
        else -> {
            intString
        }
    }
}