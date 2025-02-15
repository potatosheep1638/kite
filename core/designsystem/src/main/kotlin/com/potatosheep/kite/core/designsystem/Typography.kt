package com.potatosheep.kite.core.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class KiteTypography {

    val screenHeadline = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 36.sp,
        letterSpacing = -(0.5).sp,
    )

    val title = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        letterSpacing = -(0.1).sp,
    )

    val titleSmall = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 18.sp,
        letterSpacing = -(0.2).sp,
    )

    val body = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = -(0.1).sp,
    )

    val bodyLarge = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        letterSpacing = -(0.2).sp,
    )

    val bodySmall = TextStyle.Default.copy(
        fontFamily = KiteFonts.Inter,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        letterSpacing = -(0.1).sp,
    )
}