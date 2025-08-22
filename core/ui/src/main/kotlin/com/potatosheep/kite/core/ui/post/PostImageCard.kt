package com.potatosheep.kite.core.ui.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.transformations
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.BlurTransformation
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.markdown.MarkdownText
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.DynamicAsyncImage
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

@Composable
fun ImageCard(
    post: Post,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    blurImage: Boolean = false,
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
    previewMode: Boolean = false // TODO: Implement this
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
        onSubredditLongClick = onSubredditLongClick,
        shape = shape,
        colors = colors
    ) {
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(post.mediaLinks.first().link)
            .transformations(
                if (blurImage) {
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
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onImageClick(
                        post.mediaLinks.map { it.link },
                        post.mediaLinks.map { it.caption }
                    )
                },
        )

        if (showText && post.textContent.isNotEmpty()) {
            MarkdownText(
                text = post.textContent,
                modifier = Modifier.padding(
                    bottom = 12.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun ImageCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface {
            ImageCard(
                post = posts[1],
                onClick = {},
                onLongClick = {},
                onImageClick = { _, _ -> },
                onSubredditClick = {},
                onUserClick = {},
                onFlairClick = { _, _, _, _ -> },
                onShareClick = {},
                onBookmarkClick = {},
                previewMode = true
            )
        }
    }
}