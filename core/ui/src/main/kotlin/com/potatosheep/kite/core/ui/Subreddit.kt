package com.potatosheep.kite.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.R
import com.potatosheep.kite.core.model.Subreddit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubredditRow(
    subreddit: Subreddit,
    onClick: (String) -> Unit,
    iconButtonIcon: ImageVector,
    modifier: Modifier = Modifier,
    showIconButton: Boolean = true,
    onIconButtonClick: () -> Unit = {},
    onLongClick: (String) -> Unit = {}
) {
    Box(
        Modifier
            .combinedClickable(
                onClick = { onClick(subreddit.subredditName) },
                onLongClick = { onLongClick(subreddit.subredditName) }
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.clip(CircleShape)) {

                if (subreddit.iconLink.isNotBlank()) {
                    AsyncImage(
                        model = subreddit.iconLink,
                        placeholder = painterResource(id = R.drawable.image),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.image),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                    )
                }
            }

            Column(Modifier.padding(start = 24.dp).weight(1f)) {
                Text(
                    text = subreddit.subredditName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                )

                if (subreddit.description.isNotBlank()) {
                    Text(
                        text = subreddit.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            AnimatedVisibility(
                visible = showIconButton,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onIconButtonClick,
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = iconButtonIcon,
                            contentDescription = "",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SubredditRowPreview() {
    val subreddit = Subreddit(
        subredditName = "placeholder",
        subscribers = 3834987,
        activeUsers = 22394,
        iconLink = "",
        description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
        sidebar = "RULE 1: blah blah blah",
    )

    KiteTheme {
        Surface {
            SubredditRow(
                subreddit = subreddit,
                onClick = {},
                iconButtonIcon = KiteIcons.Add
            )
        }
    }
}