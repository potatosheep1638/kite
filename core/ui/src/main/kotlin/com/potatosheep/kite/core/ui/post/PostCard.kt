package com.potatosheep.kite.core.ui.post

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post

@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String, String) -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    showText: Boolean = false,
    blurImage: Boolean = true,
    isBookmarked: Boolean = false,
    galleryRedirect: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
) {
    if (post.mediaLinks.isEmpty()) {
        PostDefaultCard(
            post = post,
            onClick = onClick,
            onLongClick = onLongClick,
            onSubredditClick = onSubredditClick,
            onUserClick = onUserClick,
            onFlairClick = onFlairClick,
            onShareClick = onShareClick,
            onBookmarkClick = onBookmarkClick,
            modifier = modifier,
            showText = showText,
            isBookmarked = isBookmarked,
            shape = shape,
            colors = colors
        )
    } else {
        when (post.mediaLinks.first().mediaType) {
            MediaType.IMAGE -> {
                ImageCard(
                    post = post,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onImageClick = onImageClick,
                    onSubredditClick = onSubredditClick,
                    onUserClick = onUserClick,
                    onFlairClick = onFlairClick,
                    onShareClick = onShareClick,
                    onBookmarkClick = onBookmarkClick,
                    modifier = modifier,
                    showText = showText,
                    blurImage = blurImage,
                    isBookmarked = isBookmarked,
                    shape = shape,
                    colors = colors
                )
            }

            MediaType.GALLERY_THUMBNAIL, MediaType.GALLERY_IMAGE -> {
                GalleryCard(
                    post = post,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onSubredditClick = onSubredditClick,
                    onUserClick = onUserClick,
                    onImageClick =
                        if (galleryRedirect) {
                            null
                        } else {
                            onImageClick
                        },
                    onFlairClick = onFlairClick,
                    onShareClick = onShareClick,
                    onBookmarkClick = onBookmarkClick,
                    modifier = modifier,
                    showText = showText,
                    blurThumbnail = blurImage,
                    isBookmarked = isBookmarked,
                    shape = shape,
                    colors = colors
                )
            }

            MediaType.ARTICLE_THUMBNAIL -> {
                ArticleCard(
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
                    colors = colors
                )
            }

            MediaType.VIDEO_THUMBNAIL -> {
                VideoCard(
                    post = post,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onVideoClick = onVideoClick,
                    onSubredditClick = onSubredditClick,
                    onUserClick = onUserClick,
                    onFlairClick = onFlairClick,
                    onShareClick = onShareClick,
                    onBookmarkClick = onBookmarkClick,
                    modifier = modifier,
                    blurThumbnail = blurImage,
                    isBookmarked = isBookmarked,
                    shape = shape,
                    colors = colors
                )
            }

            else -> {
                PostDefaultCard(
                    post = post,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    onSubredditClick = onSubredditClick,
                    onUserClick = onUserClick,
                    onFlairClick = onFlairClick,
                    onShareClick = onShareClick,
                    onBookmarkClick = onBookmarkClick,
                    modifier = modifier,
                    showText = showText,
                    isBookmarked = isBookmarked,
                    shape = shape,
                    colors = colors
                )
            }
        }
    }
}