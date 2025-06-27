package com.potatosheep.kite.feature.library

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.util.LocalSnackbarHostState
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.NoResultsMsg
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.ui.SubredditRow
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.common.R.string as commonStrings

@Composable
fun HomeRoute(
    onBackClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val subredditListUiState by viewModel.subredditListUiState.collectAsStateWithLifecycle()

    HomeScreen(
        onBackClick = onBackClick,
        subredditListUiState = subredditListUiState,
        onSubredditClick = onSubredditClick,
        onSavedClick = onSavedClick,
        setSubreddit = viewModel::setSubreddit,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    onBackClick: () -> Unit,
    subredditListUiState: SubredditListUiState,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    setSubreddit: (Subreddit, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackClick()
    }

    val snackbarState = LocalSnackbarHostState.current
    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    when (subredditListUiState) {
        SubredditListUiState.Loading -> Unit
        is SubredditListUiState.Success -> {
            LazyColumn(
                state = listState,
                modifier = modifier
            ) {
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSavedClick() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .padding(
                                        start = 24.dp,
                                        top = 6.dp,
                                        bottom = 6.dp
                                    )
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            ) {
                                Icon(
                                    imageVector = KiteIcons.Bookmark,
                                    contentDescription = stringResource(commonStrings.library_saved),
                                    modifier = Modifier
                                        .padding(12.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = stringResource(commonStrings.library_saved),
                                    modifier = Modifier.padding(start = 12.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Text(
                                    text = stringResource(commonStrings.library_saved_description),
                                    modifier = Modifier.padding(start = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                item {
                    if (subredditListUiState.subreddits.isNotEmpty()) {
                        Text(
                            text = stringResource(commonStrings.subreddits),
                            modifier = Modifier.padding(
                                start = 24.dp,
                                top = 24.dp,
                                bottom = 12.dp,
                                end = 24.dp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                itemsIndexed(
                    items = subredditListUiState.subreddits,
                    key = { _, subreddit -> subreddit.subredditName }
                ) { _, subreddit ->
                    val itemVisibility = remember { Animatable(0f) }

                    LaunchedEffect(Unit) {
                        itemVisibility.animateTo(1f, tween(100))
                    }

                    val msg = stringResource(commonStrings.library_remove_subreddit_msg)
                    val actionLabel = stringResource(commonStrings.undo)

                    SubredditRow(
                        subreddit = subreddit,
                        onClick = {
                            onSubredditClick(subreddit.subredditName)
                        },
                        iconButtonIcon = KiteIcons.Clear,
                        modifier = Modifier
                            .padding(
                                horizontal = 24.dp,
                                vertical = 14.dp
                            )
                            .alpha(itemVisibility.value)
                        ,
                        onIconButtonClick = {
                            coroutineScope.launch {
                                itemVisibility.animateTo(0f, tween(100))
                                setSubreddit(subreddit, false)

                                val result = snackbarState.showSnackbar(
                                    message = msg,
                                    actionLabel = actionLabel,
                                    duration = SnackbarDuration.Short
                                )

                                when (result) {
                                    SnackbarResult.ActionPerformed -> {
                                        setSubreddit(subreddit, true)
                                    }

                                    SnackbarResult.Dismissed -> Unit
                                }
                            }
                        }
                    )
                }
            }

            if (subredditListUiState.subreddits.isEmpty()) {
                NoResultsMsg(
                    title = stringResource(commonStrings.no_result),
                    subtitle = stringResource(commonStrings.library_no_subreddits_msg),
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Preview
@Composable
fun LibraryScreenPreview() {
    val subreddits = listOf(
        Subreddit(
            subredditName = "placeholder",
            subscribers = 3834987,
            activeUsers = 22394,
            iconLink = "https://redlib.example.com/style/blahblah.jpg",
            description = "This subreddit is a placeholder subreddit. If you are not a placeholder, get out.",
            sidebar = "RULE 1: blah blah blah",
        )
    )

    KiteTheme {
        HomeScreen(
            onBackClick = {},
            subredditListUiState = SubredditListUiState.Success(subreddits),
            onSubredditClick = {},
            onSavedClick = {},
            setSubreddit = { _, _ -> },
            modifier = Modifier
        )
    }
}