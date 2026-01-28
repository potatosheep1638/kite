package com.potatosheep.kite.feature.subreddit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.R
import com.potatosheep.kite.core.designsystem.SmallTopAppBar
import com.potatosheep.kite.core.markdown.MarkdownText
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.translation.R.string as Translation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun SubredditAboutContent(
    subreddit: Subreddit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                backIcon = Icons.AutoMirrored.Rounded.ArrowBack,
                onBackClick = onBackClick,
                title = stringResource(Translation.about),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = if (scrollBehavior != null) {
            modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            modifier
        },
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal
                    )
                )
        ) {
            with(sharedTransitionScope) {
                val sharedBoundsModifier =
                    if (this != null && animatedVisibilityScope != null) {
                        Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "about"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    } else {
                        Modifier
                    }

                val screenScrollState = rememberScrollState()

                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(screenScrollState)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        if (subreddit.iconLink.isNotBlank()) {
                            AsyncImage(
                                model = subreddit.iconLink,
                                contentDescription = null,
                                modifier = Modifier
                                    .then(sharedBoundsModifier)
                                    .clip(CircleShape)
                                    .size(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.image),
                                contentDescription = null,
                                modifier = modifier
                                    .then(sharedBoundsModifier)
                                    .clip(CircleShape)
                                    .size(150.dp)
                            )
                        }
                    }

                    Text(
                        text = subreddit.subredditName,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth(),
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        top = 24.dp,
                                        end = 12.dp,
                                        bottom = 24.dp
                                    )
                                    .width(150.dp)
                            ) {
                                Icon(
                                    imageVector = KiteIcons.Subscribers,
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )

                                Text(
                                    text = stringResource(Translation.subreddit_following_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )

                                Text(
                                    text = "${subreddit.subscribers}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 24.dp, end = 12.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Left,
                                )
                            }


                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(
                                        start = 12.dp,
                                        top = 24.dp,
                                        end = 12.dp,
                                        bottom = 24.dp
                                    )
                                    .width(150.dp)
                            ) {
                                Icon(
                                    imageVector = KiteIcons.ActiveUsers,
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = stringResource(Translation.subreddit_online_label),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Text(
                                    text = "${subreddit.activeUsers}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .padding(start = 12.dp, top = 24.dp, end = 12.dp),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    textAlign = TextAlign.Left,
                                )
                            }
                        }
                    }

                    MarkdownText(
                        text = subreddit.description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .fillMaxWidth()
                    )

                    if (subreddit.description.isNotBlank()) {
                        HorizontalDivider(
                            thickness = Dp.Hairline,
                            modifier = Modifier.padding(
                                horizontal = 48.dp,
                                vertical = 24.dp
                            )
                        )
                    }

                    MarkdownText(
                        text = subreddit.sidebar,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(
                                top = 0.dp
                            )
                            .fillMaxWidth()
                    )
                }
            }

            BackHandler {
                onBackClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun SubredditAboutPreview() {

    val subreddit = Subreddit(
        subredditName = "placeholder",
        subscribers = 3834987,
        activeUsers = 22394,
        iconLink = "https://redlib.example.com/style/blahblah.jpg",
        description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
        sidebar = "RULE 1: blah blah blah",
    )

    KiteTheme {
        SubredditAboutContent(
            subreddit = subreddit,
            onBackClick = {},
        )
    }
}