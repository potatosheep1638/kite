package com.potatosheep.kite.core.markdown

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import com.potatosheep.kite.core.markdown.util.markdownRenderer

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    val renderer = LocalContext.current.markdownRenderer
    val textString = rememberSaveable { text }

    val textColor = color.takeOrElse {
        style.color.takeOrElse {
            LocalContentColor.current
        }
    }

    val mergedStyle = style.merge(
        color = textColor,
        fontSize = fontSize,
        textAlign = textAlign ?: TextAlign.Unspecified,
        lineHeight = lineHeight
    )

    Box(modifier) {
        AndroidView(
            factory = { ctx ->
                TextView(ctx).apply {
                    setTextColor(mergedStyle.color.toArgb())
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)
                    mergedStyle.textAlign.let { alignment ->
                        textAlignment = when (alignment) {
                            TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                            TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                            TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                            else -> View.TEXT_ALIGNMENT_TEXT_START
                        }
                    }
                    setLineSpacing(mergedStyle.lineHeight.value, 1f)

                    isLongClickable = true

                    setOnClickListener {
                        if (this.selectionStart == -1 && this.selectionEnd == -1) {
                            onClick()
                        }
                    }

                    setOnLongClickListener {
                        onLongClick()
                        return@setOnLongClickListener true
                    }

                    renderer.setMarkdown(this, textString)
                }
            },
            modifier = modifier,
            update = {},
        )
    }
}