package com.potatosheep.kite.core.ui.post

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.DynamicAsyncImage
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

@Composable
fun ArticleCard(
    post: Post,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onSubredditLongClick: (String) -> Unit = {},
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surface
    ),
) {
    val context = LocalContext.current

    BasePostCard(
        post = post,
        onClick = onClick,
        onLongClick = onLongClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onFlairClick = onFlairClick,
        onShareClick = onShareClick,
        onBookmarkClick = onBookmarkClick,
        modifier = modifier,
        isBookmarked = isBookmarked,
        onSubredditLongClick = onSubredditLongClick,
        shape = shape,
        colors = colors,
        titleTrailingComposable = {
            Row {
                Box(
                    Modifier
                        .width(IntrinsicSize.Min)
                        .padding(top = 6.dp, start = 12.dp)
                ) {
                    val link = post.mediaLinks[1].link

                    if (post.mediaLinks.first().link.isEmpty()) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .clickable {
                                    uriHandler(link, context)
                                }
                        ) {
                            Icon(
                                imageVector = KiteIcons.Link,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        DynamicAsyncImage(
                            model = post.mediaLinks.first().link,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .size(100.dp)
                                .clickable {
                                    uriHandler(link, context)
                                }
                        )
                    }

                    ElevatedCard(
                        modifier = Modifier
                            .alignToRightOfParent(
                                x = 4.dp,
                                y = 4.dp
                            ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor =
                            if (post.mediaLinks[1].link.isNotEmpty())
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Icon(
                            imageVector = KiteIcons.ExitToApp,
                            contentDescription = "External link",
                            tint =
                            if (post.mediaLinks[1].link.isNotEmpty())
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    ) {
        Text(
            text =
            if (post.mediaLinks[1].link.isNotEmpty())
                post.mediaLinks[1].link.split("/")[2]
            else
                "Error: Invalid link",
            modifier = Modifier.padding(
                bottom = 12.dp
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun uriHandler(
    uri: String,
    context: Context
) {
    val link = uri.toUri()
    val intent = Intent(Intent.ACTION_VIEW, link)

    context.startActivity(intent)
}

@Preview
@Composable
private fun GalleryCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface {
            ArticleCard(
                post = posts[4],
                onClick = {},
                onLongClick = {},
                onSubredditClick = {},
                onUserClick = {},
                onFlairClick = { _, _, _, _ -> },
                onBookmarkClick = {},
                onShareClick = {}
            )
        }
    }
}
