package com.potatosheep.kite.feature.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.potatosheep.kite.core.common.enums.MenuOption
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteDropdownMenu
import com.potatosheep.kite.core.designsystem.KiteDropdownMenuItem
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteTheme
import com.potatosheep.kite.core.designsystem.KiteTopAppBar
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.designsystem.NoResultsMsg
import com.potatosheep.kite.core.model.Subreddit
import com.potatosheep.kite.core.ui.SubredditRow
import kotlinx.coroutines.launch
import com.potatosheep.kite.core.common.R.string as commonStrings

@Composable
fun LibraryRoute(
    onSettingsClick: () -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    onAboutClick: () -> Unit,
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val subredditListUiState by viewModel.subredditListUiState.collectAsStateWithLifecycle()

    LibraryScreen(
        subredditListUiState = subredditListUiState,
        onSettingsClick = onSettingsClick,
        onSearchClick = onSearchClick,
        onSubredditClick = onSubredditClick,
        onSavedClick = onSavedClick,
        onAboutClick = onAboutClick,
        removeSubreddit = viewModel::removeSubreddit,
        navBar = navBar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onSettingsClick: () -> Unit,
    subredditListUiState: SubredditListUiState,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    onAboutClick: () -> Unit,
    removeSubreddit: (Subreddit) -> Unit,
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var isMenuExpanded by remember { mutableStateOf(false) }

    val snackbarState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val subredditMarkedForDeletion = remember { mutableStateListOf<Subreddit>() }

    DisposableEffect(Unit) {
        onDispose {
            if (!subredditMarkedForDeletion.isEmpty()) {
                subredditMarkedForDeletion.forEach {
                    removeSubreddit(it)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            KiteTopAppBar(
                title = "kite",
                searchIcon = KiteIcons.Search,
                searchIconContentDescription = stringResource(com.potatosheep.kite.core.common.R.string.content_desc_search),
                optionsIcon = KiteIcons.MoreOptions,
                optionsContentDescription = stringResource(com.potatosheep.kite.core.common.R.string.content_desc_more_options),
                onSearchClick = {
                    onSearchClick(
                        SortOption.Search.RELEVANCE,
                        SortOption.Timeframe.ALL,
                        null,
                        ""
                    )
                },
                onOptionsClick = { isMenuExpanded = true },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LocalBackgroundColor.current
                ),
                scrollBehavior = scrollBehavior
            )

            Box(Modifier.alignToRightOfParent()) {
                val menuOptions = MenuOption.entries

                KiteDropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    menuOptions.forEach { option ->
                        when (option) {
                            MenuOption.SORT -> Unit

                            else -> {
                                KiteDropdownMenuItem(
                                    text = option.label,
                                    onClick =
                                    when (option) {
                                        MenuOption.SETTINGS -> {
                                            {
                                                onSettingsClick()
                                                isMenuExpanded = false
                                            }
                                        }

                                        MenuOption.ABOUT -> {
                                            {
                                                onAboutClick()
                                                isMenuExpanded = false
                                            }
                                        }

                                        else -> {{}}
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = navBar,
        snackbarHost = { SnackbarHost(hostState = snackbarState) },
        containerColor = LocalBackgroundColor.current,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
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
            when (subredditListUiState) {
                SubredditListUiState.Loading -> Unit
                is SubredditListUiState.Success -> {
                    LazyColumn(
                        state = listState
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
                            var showPost by remember { mutableStateOf(true) }

                            AnimatedVisibility(
                                visible = showPost,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                val msg = stringResource(commonStrings.library_remove_subreddit_msg)
                                val actionLabel = stringResource(commonStrings.undo)

                                SubredditRow(
                                    subreddit = subreddit,
                                    onClick = {
                                        onSubredditClick(subreddit.subredditName)
                                    },
                                    iconButtonIcon = KiteIcons.Clear,
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp,
                                        vertical = 14.dp
                                    ),
                                    onIconButtonClick = {
                                        coroutineScope.launch {
                                            showPost = false
                                            subredditMarkedForDeletion.add(subreddit)

                                            val result = snackbarState.showSnackbar(
                                                message = msg,
                                                actionLabel = actionLabel,
                                                duration = SnackbarDuration.Short,

                                                )

                                            when (result) {
                                                SnackbarResult.ActionPerformed -> {
                                                    showPost = true
                                                    subredditMarkedForDeletion.remove(subreddit)
                                                }

                                                SnackbarResult.Dismissed -> {
                                                    subredditMarkedForDeletion.remove(subreddit)
                                                    removeSubreddit(subreddit)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (subredditListUiState.subreddits.isEmpty()) {
                        NoResultsMsg(
                            title = stringResource(commonStrings.no_result),
                            subtitle = stringResource(commonStrings.library_no_subreddits_msg),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                    }
                }
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
        LibraryScreen(
            subredditListUiState = SubredditListUiState.Success(subreddits),
            onSettingsClick = {},
            onSearchClick = { _, _, _, _ -> },
            onSubredditClick = {},
            onSavedClick = {},
            onAboutClick = {},
            removeSubreddit = {},
            navBar = {},
            modifier = Modifier
        )
    }
}