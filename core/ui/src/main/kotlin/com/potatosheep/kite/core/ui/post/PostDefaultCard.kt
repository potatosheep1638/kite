package com.potatosheep.kite.core.ui.post

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.markdown.MarkdownText
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

@Composable
fun PostDefaultCard(
    post: Post,
    onClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    isBookmarked: Boolean = false,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
        if (isSystemInDarkTheme())
            MaterialTheme.colorScheme.surfaceContainerHigh
        else
            MaterialTheme.colorScheme.surface
    )
) {
    BasePostCard(
        post = post,
        onClick = onClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onFlairClick = onFlairClick,
        onShareClick = onShareClick,
        onBookmarkClick = onBookmarkClick,
        modifier = modifier,
        isBookmarked = isBookmarked,
        shape = shape,
        colors = colors
    ) {
        if (showText && post.textContent.isNotEmpty()) {
            MarkdownText(
                text = post.textContent,
                modifier = Modifier
                    .padding(
                        bottom = 12.dp
                    )
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun PostDefaultCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface {
            PostDefaultCard(
                post = posts[0],
                onClick = {},
                onSubredditClick = {},
                onUserClick = {},
                onFlairClick = { _, _, _, _ -> },
                onShareClick = {},
                onBookmarkClick = {}
            )
        }
    }
}