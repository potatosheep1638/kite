package com.potatosheep.kite.core.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.abbreviate
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.NsfwTag
import com.potatosheep.kite.core.designsystem.SpoilerTag
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.Flair
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

@Composable
internal fun BasePostCard(
    post: Post,
    onClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surface
    ),
    titleTrailingComposable: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Column(
            Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = post.subredditName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(
                            top = 12.dp,
                            bottom = 6.dp
                        )
                        .clickable { onSubredditClick(post.subredditName) }
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        bottom = 12.dp
                    )
                    .fillMaxWidth()
            ) {
                Text(
                    text = post.userName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        onUserClick(
                            if (post.userName.contains("u/"))
                                post.userName.split("u/")[1]
                            else
                                post.userName
                        )
                    }
                )

                Text(
                    text = " • ${post.timeAgo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (post.flair.isNotEmpty()) {
                    Flair(
                        flairComponents = post.flair,
                        onClick = {
                            onFlairClick(
                                SortOption.Search.RELEVANCE,
                                SortOption.Timeframe.ALL,
                                post.subredditName,
                                "flair_name:\"${post.flairId}\""
                            )
                        },
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                if (post.isNsfw) {
                    NsfwTag(
                        Modifier.padding(end = 6.dp)
                    )
                }

                if (post.isSpoiler) {
                    SpoilerTag()
                }
            }

            Row {
                Text(
                    text = post.title,
                    fontFamily = KiteFonts.InterMedium,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(
                            top = 2.dp,
                            bottom = 16.dp
                        )
                        .weight(1f)
                )

                titleTrailingComposable()
            }

            content()

            Row(
                Modifier.padding(
                    top = 8.dp,
                )
            ) {
                Box(
                    Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(34.dp)
                        .background(
                            if (isSystemInDarkTheme())
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 6.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = KiteIcons.Upvotes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 1.dp),
                        )

                        Text(
                            text =
                            if (post.upvoteCount != -Int.MAX_VALUE)
                                post.upvoteCount.abbreviate()
                            else
                                "Hidden",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .height(34.dp)
                        .background(
                            if (isSystemInDarkTheme())
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            else
                                MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 12.dp,
                            bottom = 4.dp
                        )
                    ) {
                        Icon(
                            imageVector = KiteIcons.ChatBubble,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(26.dp)
                                .padding(
                                    top = 5.dp,
                                    bottom = 4.dp
                                )
                        )

                        Text(
                            text = post.commentCount.abbreviate(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Box(
                    Modifier
                        .clip(CircleShape)
                        .size(34.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary
                        )
                ) {
                    IconButton(
                        onClick = onShareClick
                    ) {
                        Icon(
                            imageVector = KiteIcons.Share,
                            contentDescription = "Share post",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 2.dp)
                        )
                    }
                }

                Spacer(
                    Modifier
                        .padding(end = 6.dp)
                )

                Box(
                    Modifier
                        .clip(CircleShape)
                        .size(34.dp)
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                ) {
                    IconButton(
                        onClick = onBookmarkClick
                    ) {
                        if (isBookmarked) {
                            Icon(
                                imageVector = KiteIcons.Bookmarked,
                                contentDescription = "Un-bookmark post",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = KiteIcons.Bookmark,
                                contentDescription = "Bookmark post",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BasePostCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface {
            BasePostCard(
                post = posts[0],
                onClick = {},
                onSubredditClick = {},
                onUserClick = {},
                onFlairClick = { _, _, _, _ -> },
                onShareClick = {},
                onBookmarkClick = {}
            ) {
                Text("test")
            }
        }
    }
}