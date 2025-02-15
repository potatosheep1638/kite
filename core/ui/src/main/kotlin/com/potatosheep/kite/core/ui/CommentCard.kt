package com.potatosheep.kite.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.designsystem.IndentColor
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.markdown.MarkdownText
import com.potatosheep.kite.core.model.Comment
import com.potatosheep.kite.core.ui.param.CommentListPreviewParameterProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentCard(
    comment: Comment,
    shape: Shape,
    modifier: Modifier = Modifier,
    isTopLevelComment: Boolean = false,
    indents: Int = 0,
    onLongClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    expanded: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
) {
    val indentColors = IndentColor.entries

    Card(
        shape = shape,
        colors = colors,
        modifier = modifier
            .combinedClickable(
                onClick = {
                    if (!expanded) {
                        onLongClick()
                    }
                },
                onLongClick = onLongClick,
                onLongClickLabel = ""
            )
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start =
                    if (isTopLevelComment) {
                        0.dp
                    } else {
                        10.dp + (6.dp * indents)
                    }
                )
                .drawBehind {
                    if (!isTopLevelComment) {
                        drawLine(
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            color = indentColors[indents - 1].color,
                            strokeWidth = 8f
                        )
                    }
                }
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 6.dp,
                    end = 16.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            top = 4.dp
                        )
                        .fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (comment.isPostAuthor) {
                                Box(
                                    Modifier
                                        .padding(vertical = 6.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            onUserClick(comment.userName.substringAfter("u/"))
                                        }
                                ) {
                                    Text(
                                        text = comment.userName,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 5.dp
                                        ),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            } else {
                                Text(
                                    text = comment.userName,
                                    modifier = Modifier
                                        .padding(vertical = 6.dp)
                                        .clickable {
                                            onUserClick(comment.userName.substringAfter("u/"))
                                        },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            Text(
                                text = " • ${comment.timeAgo}",
                                modifier = Modifier.padding(vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )

                            if (!expanded) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(
                                        imageVector = KiteIcons.Expand,
                                        contentDescription = "Show replies",
                                        tint = MaterialTheme.colorScheme.onSecondary,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(
                            top = 12.dp,
                        )
                        .fillMaxWidth()
                ) {
                    if (expanded) {
                        MarkdownText(
                            text = comment.textContent,
                            modifier = Modifier.fillMaxWidth(),
                            onLongClick = onLongClick,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Box(
                            Modifier
                                .padding(
                                    top = 12.dp,
                                    bottom = 8.dp
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    top = 4.dp,
                                    bottom =
                                    if (comment.postTitle.isNullOrBlank())
                                        0.dp
                                    else
                                        6.dp

                                )
                            ) {
                                Icon(
                                    imageVector = KiteIcons.Upvotes,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(22.dp),
                                )

                                Text(
                                    text = if (comment.upvoteCount != -Int.MAX_VALUE)
                                        comment.upvoteCount.toString()
                                    else
                                        "Hidden",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCommentCard(
    comment: Comment,
    onClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        colors = colors,
        shape = shape
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp
            )
        ) {
            Row(
                Modifier
                    .padding(top = 6.dp)
                    .clickable { onSubredditClick(comment.subredditName) }
            ) {
                Text(
                    text = "${comment.subredditName.substringAfter("/")}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Text(
                    text = " • ${comment.timeAgo}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = comment.postTitle!!,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontFamily = KiteFonts.InterMedium,
                    modifier = Modifier.weight(
                        weight = 1f,
                        fill = false
                    )
                )
            }

            Column(
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        bottom = 6.dp
                    )
                    .fillMaxWidth()
            ) {
                MarkdownText(
                    text = comment.textContent,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )

                Box(
                    Modifier
                        .padding(
                            top = 12.dp
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 0.dp
                        )
                    ) {
                        Icon(
                            imageVector = KiteIcons.Upvotes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(22.dp),
                        )

                        Text(
                            text = comment.upvoteCount.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CommentCardPreview(
    @PreviewParameter(CommentListPreviewParameterProvider::class)
    comments: List<Comment>
) {
    KiteTheme {
        Surface(color = LocalBackgroundColor.current) {
            Column {
                val indents = listOf(0, 1, 2)

                comments.forEachIndexed { i, comment ->
                    if (i < 3) {
                        CommentCard(
                            comment = comment,
                            modifier = Modifier
                                .padding(
                                    horizontal = 12.dp
                                ),
                            isTopLevelComment = i == 0,
                            indents = indents[i],
                            shape = RectangleShape
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CommentInUserPagePreview(
    @PreviewParameter(CommentListPreviewParameterProvider::class)
    comments: List<Comment>
) {
    KiteTheme {
        Surface(color = LocalBackgroundColor.current) {
            UserCommentCard(
                comment = comments[3],
                onClick = {},
                onSubredditClick = {},
                modifier = Modifier.padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}