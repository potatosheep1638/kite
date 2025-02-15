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

val Icons.Rounded.Bookmark: ImageVector
    get() {
        if (_bookmarkfilled != null) {
            return _bookmarkfilled!!
        }
        _bookmarkfilled = Builder(name = "Bookmarkfilled", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f).apply {
            path(fill = SolidColor(Color(0xFF5f6368)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(200.0f, 840.0f)
                verticalLineToRelative(-640.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(280.0f, 120.0f)
                horizontalLineToRelative(400.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(760.0f, 200.0f)
                verticalLineToRelative(640.0f)
                lineTo(480.0f, 720.0f)
                lineTo(200.0f, 840.0f)
                close()
            }
        }
        .build()
        return _bookmarkfilled!!
    }

private var _bookmarkfilled: ImageVector? = null
