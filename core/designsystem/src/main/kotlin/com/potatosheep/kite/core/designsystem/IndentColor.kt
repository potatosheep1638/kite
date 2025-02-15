package com.potatosheep.kite.core.designsystem

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
private val PastelPink = Color(0xFFFDBCCF)

@Stable
private val PastelYellow = Color(0xFFE7FFAC)

@Stable
private val PastelGreen = Color(0xFF97C1A9)

@Stable
private val PastelBlue = Color(0xFFBFF4FF)

@Stable
private val PastelPurple = Color(0xFFB1A2CA)

@Stable
private val WarmBerry = Color(0xFF85586F)

@Stable
private val SoftRaspberry = Color(0xFFAC7D88)

@Stable
private val PaleOrange = Color(0xFFDEB6AB)

@Stable
private val SoftYellow = Color(0xFFF8ECD1)

@Stable
private val PaleSeaBlue = Color(0xFFD6EFED)

enum class IndentColor(val color: Color) {
    ONE(PastelPink),
    TWO(PastelYellow),
    THREE(PastelGreen),
    FOUR(PastelBlue),
    FIVE(PastelPurple),
    SIX(WarmBerry),
    SEVEN(SoftRaspberry),
    EIGHT(PaleOrange),
    NINE(SoftYellow),
    TEN(PaleSeaBlue)
}