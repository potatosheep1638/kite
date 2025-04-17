package com.potatosheep.kite.core.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.transformations
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.BlurTransformation
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.markdown.MarkdownText
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.DynamicAsyncImage
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

@Composable
fun GalleryCard(
    post: Post,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    blurThumbnail: Boolean = false,
    isBookmarked: Boolean = false,
    onImageClick: ((List<String>, List<String?>) -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surface
    ),
) {
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
        shape = shape,
        colors = colors,
        titleTrailingComposable = {
            Row {
                Box(
                    Modifier
                        .width(IntrinsicSize.Min)
                        .padding(top = 6.dp, start = 12.dp)
                ) {
                    if (post.mediaLinks.first().link.isEmpty()) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .clickable(onClick = onClick)
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
                        val imageRequest = ImageRequest.Builder(LocalContext.current)
                            .data(post.mediaLinks.first().link)
                            .transformations(
                                if (blurThumbnail) {
                                    listOf(
                                        BlurTransformation(
                                            radius = 100,
                                            scale = 1f
                                        )
                                    )
                                } else {
                                    emptyList()
                                }
                            )
                            .build()

                        DynamicAsyncImage(
                            model = imageRequest,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .size(100.dp)
                                .clickable {
                                    if (onImageClick == null) {
                                        onClick()
                                    } else {
                                        onImageClick(
                                            post.mediaLinks.map { it.link },
                                            post.mediaLinks.map { it.caption }
                                        )
                                    }
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
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 3.dp
                        )
                    ) {
                        Icon(
                            painter = painterResource(KiteIcons.Image),
                            contentDescription = "Gallery",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                }
            }
        }
    ) {
        if (showText && post.textContent.isNotEmpty()) {
            MarkdownText(
                text = post.textContent,
                modifier = Modifier
                    .padding(
                        top = 14.dp,
                        bottom = 12.dp
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Spacer(
                Modifier.padding(
                    start = 18.dp,
                    bottom = 12.dp
                )
            )
        }
    }
}

@Preview
@Composable
private fun GalleryCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface {
            GalleryCard(
                post = posts[1],
                onClick = {},
                onLongClick = {},
                onSubredditClick = {},
                onUserClick = {},
                onFlairClick = { _, _, _, _ -> },
                onShareClick = {},
                onBookmarkClick = {}
            )
        }
    }
}
