package com.potatosheep.kite.core.common.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.alignToRightOfParent(x: Dp = 0.dp, y: Dp = 0.dp): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)

        layout(placeable.width, placeable.height) {
            placeable.place(
                x = constraints.maxWidth
                        - placeable.width
                        - x.roundToPx(),
                y = y.roundToPx()
            )
        }
    }
}