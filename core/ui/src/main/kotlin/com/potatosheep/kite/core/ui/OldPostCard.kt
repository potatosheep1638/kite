package com.potatosheep.kite.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.potatosheep.kite.core.common.util.abbreviate
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.model.MediaType
import com.potatosheep.kite.core.model.Post
import com.potatosheep.kite.core.ui.param.PostListPreviewParameterProvider

// TODO: Remove this
@Composable
fun OldPostCard(
    post: Post,
    onClick: () -> Unit,
    onImageClick: (List<String>) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(
        containerColor =
            if (isSystemInDarkTheme())
                MaterialTheme.colorScheme.surfaceContainerHigh
            else
                MaterialTheme.colorScheme.surface
    ),
    previewMode: Boolean = false,
    hideLongText: Boolean = false,
    showIndication: Boolean = true
) {
    Card(
        colors = colors,
        shape = shape,
        modifier = modifier.clickable(
            onClick = onClick,
            interactionSource = null,
            indication = if (showIndication) {
                LocalIndication.current
            } else {
                null
            }
        )
    ) {
        Column (modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Posted in ${post.subredditName}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(
                        top = 12.dp,
                        start = 6.dp,
                        end = 6.dp,
                        bottom = 6.dp
                    )
                    .clickable(
                        onClick = { onSubredditClick(post.subredditName) }
                    )
            )

            Column(
                modifier = Modifier
                    .padding(
                        start = 6.dp,
                        end = 6.dp
                    )
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = KiteFonts.Inter,
                    modifier = Modifier.padding(
                        top = 6.dp
                    )
                )

                Row (
                    modifier = Modifier
                        .padding(
                            top = 6.dp,
                            bottom = 12.dp
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.labelMedium,
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
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (post.textContent.isNotEmpty() && post.mediaLinks.isEmpty()) {
                    BoxWithConstraints {
                        val textMeasurer = rememberTextMeasurer()
                        val lineCount = textMeasurer
                            .measure(
                                text = post.textContent,
                                style = TextStyle(fontSize = 15.sp),
                                constraints = constraints
                            )
                            .lineCount

                        if (!hideLongText) {
                            Text(
                                text = post.textContent,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(
                                    top = 6.dp,
                                    bottom = 6.dp
                                )
                            )
                        }
                    }
                }
            }

            if (post.mediaLinks.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (post.mediaLinks.first().link == "") {
                        Image(
                            painter = painterResource(KiteIcons.Image),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 6.dp, bottom = 14.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    } else {
                        DynamicAsyncImage(
                            model = post.mediaLinks.first().link,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 6.dp, bottom = 14.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .fillMaxWidth()
                                .height(
                                    if (previewMode)
                                        200.dp
                                    else
                                        Dp.Unspecified
                                )
                                .clickable {
                                    if (post.mediaLinks.size > 1 &&
                                        post.mediaLinks[1].mediaType == MediaType.GALLERY_LINK
                                    ) {

                                        onClick()
                                    } else if (post.mediaLinks.first().mediaType == MediaType.IMAGE) {
                                        onImageClick(
                                            post.mediaLinks.map { it.link },
                                        )
                                    }
                                }
                        )
                    }

                    if (post.mediaLinks.size > 1 &&
                        post.mediaLinks.first().mediaType == MediaType.IMAGE) {

                        Box(
                            modifier = Modifier
                                .alignToRightOfParent(
                                    x = 8.dp,
                                    y = 16.dp
                                )
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(KiteIcons.Image),
                                contentDescription = "Gallery",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(25.dp)
                            )
                        }
                    } else if (post.mediaLinks.size > 1 &&
                        post.mediaLinks.first().mediaType == MediaType.ARTICLE_THUMBNAIL) {

                        ElevatedCard(
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier
                                .alignToRightOfParent(
                                    x = 8.dp,
                                    y = 16.dp
                                )
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            Text(
                                text = post.mediaLinks[1].link.split("/")[2],
                                fontSize = 14.sp,
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    top = 3.dp,
                                    end = 8.dp,
                                    bottom = 3.dp
                                )
                            )
                        }
                    }
                }
            }

            Row(modifier =
                Modifier.padding(
                    top = 8.dp,
                    bottom = 6.dp
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
                    Row(verticalAlignment = Alignment.CenterVertically,
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
                            if (post.upvoteCount != -1)
                                post.upvoteCount.abbreviate()
                            else
                                "Hidden",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = KiteFonts.Inter,
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
                    Row(verticalAlignment = Alignment.CenterVertically,
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
                            fontFamily = KiteFonts.Inter,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.unconstrainedPadding(padding: Dp): Modifier {
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth - (padding * 2).roundToPx()
            )
        )

        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

@PreviewLightDark
@Composable
private fun PostCardPreview(
    @PreviewParameter(PostListPreviewParameterProvider::class)
    posts: List<Post>
) {
    KiteTheme {
        Surface(color = LocalBackgroundColor.current) {
            OldPostCard(
                post = posts[4],
                onImageClick = {},
                onSubredditClick = {},
                onClick = {},
                onUserClick = {},
                previewMode = true,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}