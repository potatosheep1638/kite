package com.potatosheep.kite.core.designsystem.vectors

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ArrowDropUp: ImageVector
    get() {
        if (_roundArrowDropUp != null) {
            return _roundArrowDropUp!!
        }
        _roundArrowDropUp = Builder(name = "RoundArrowDropUp", defaultWidth = 24.0.dp, defaultHeight
                = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(8.71f, 12.29f)
                lineTo(11.3f, 9.7f)
                curveToRelative(0.39f, -0.39f, 1.02f, -0.39f, 1.41f, 0.0f)
                lineToRelative(2.59f, 2.59f)
                curveToRelative(0.63f, 0.63f, 0.18f, 1.71f, -0.71f, 1.71f)
                horizontalLineTo(9.41f)
                curveToRelative(-0.89f, 0.0f, -1.33f, -1.08f, -0.7f, -1.71f)
                close()
            }
        }
        .build()
        return _roundArrowDropUp!!
    }

private var _roundArrowDropUp: ImageVector? = null
