package com.potatosheep.kite.core.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.designsystem.IndentColor
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.ui.param.CommentListPreviewParameterProvider

// TODO: Move this to feature:post
@Composable
fun MoreRepliesCard(
    comment: Comment,
    onClick: (String, String, String?, String?, Boolean, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    thumbnailLink: String? = null,
    shape: Shape = RectangleShape,
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surface
    ),
    indents: Int = 0
) {
    val indentColors = IndentColor.entries

    Card(
        shape = shape,
        colors = colors,
        modifier = modifier.clickable(
            onClick = {
                onClick(
                    comment.subredditName,
                    comment.postId,
                    comment.id,
                    thumbnailLink,
                    false,
                    false
                )
            }
        )
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = 10.dp + (6.dp * indents)
                )
                .drawBehind {
                    drawLine(
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        color = indentColors[
                            if (indents > 0)
                                indents - 1
                            else
                                indents
                        ].color,
                        strokeWidth = 8f
                    )
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        vertical = 6.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = "More replies",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                Icon(
                    imageVector = KiteIcons.MoreReplies,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MoreRepliesCardPreview(
    @PreviewParameter(CommentListPreviewParameterProvider::class)
    comments: List<Comment>
) {
    KiteTheme {
        Surface {
            MoreRepliesCard(
                comment = comments[0],
                onClick = { _, _, _, _, _, _ -> }
            )
        }
    }
}