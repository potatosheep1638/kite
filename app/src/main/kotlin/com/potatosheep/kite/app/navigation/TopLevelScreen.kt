package com.potatosheep.kite.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.potatosheep.kite.core.common.TopAppBarActionState
import com.potatosheep.kite.app.MenuOption
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteDropdownMenu
import com.potatosheep.kite.core.designsystem.KiteDropdownMenuItem
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteNavigationSuiteScaffold
import com.potatosheep.kite.core.designsystem.KiteTopAppBar
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.about.api.navigation.navigateToAbout
import com.potatosheep.kite.core.translation.R.string as Translation
import com.potatosheep.kite.feature.bookmark.api.navigation.navigateToBookmark
import com.potatosheep.kite.feature.feed.impl.nav.FeedScreen
import com.potatosheep.kite.feature.home.impl.nav.HomeScreen
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.settings.api.navigation.navigateToSettings
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopLevelScreen(
    navigator: Navigator,
    snackbarHostState: SnackbarHostState,
    topAppBarActionState: TopAppBarActionState,
    modifier: Modifier = Modifier
) {
    var currentTopLevelDestination by rememberSaveable { mutableStateOf(TopLevelDestination.FEED) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    var isTitleVisible by remember { mutableStateOf(true) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var subredditScope: String? by remember { mutableStateOf(null) }

    if (currentTopLevelDestination == TopLevelDestination.HOME) {
        isTitleVisible = false
    }

    KiteNavigationSuiteScaffold(
        navigationSuiteItems = {
            TopLevelDestination.entries.forEach { destination ->
                val selected = currentTopLevelDestination == destination

                item(
                    selected = selected,
                    icon = {
                        if (selected)
                            Icon(
                                imageVector = destination.selectedIcon,
                                contentDescription = null
                            )
                        else
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null
                            )
                    },
                    label = {
                        Text(
                            text = destination.label,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    onClick = { currentTopLevelDestination = destination }
                )
            }
        },
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Scaffold(
            topBar = {
                KiteTopAppBar(
                    title =
                        if (isTitleVisible)
                            ""
                        else
                            stringResource(Translation.home_top_app_bar_title),
                    searchIcon = KiteIcons.Search,
                    searchIconContentDescription = stringResource(Translation.content_desc_search),
                    optionsIcon = KiteIcons.MoreOptions,
                    optionsContentDescription = stringResource(Translation.content_desc_more_options),
                    onSearchClick = {
                        navigator.navigateToSearch(
                            SortOption.Search.RELEVANCE,
                            SortOption.Timeframe.ALL,
                            subredditScope
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
                    val destinationIsHome = currentTopLevelDestination == TopLevelDestination.HOME
                    val destinationIsFeed = currentTopLevelDestination == TopLevelDestination.FEED

                    KiteDropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                    ) {
                        menuOptions.forEach { option ->
                            if ((option != MenuOption.SORT && destinationIsHome) || destinationIsFeed) {
                                KiteDropdownMenuItem(
                                    text = option.label,
                                    onClick =
                                        when (option) {
                                            MenuOption.SORT -> {
                                                {
                                                    topAppBarActionState.showSort = true
                                                    isMenuExpanded = false
                                                }
                                            }

                                            MenuOption.SETTINGS -> {
                                                {
                                                    navigator.navigateToSettings()
                                                    isMenuExpanded = false
                                                }
                                            }

                                            MenuOption.ABOUT -> {
                                                {
                                                    navigator.navigateToAbout()
                                                    isMenuExpanded = false
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                when (currentTopLevelDestination) {
                    TopLevelDestination.FEED -> {
                        FeedScreen(
                            onPostClick = navigator::navigateToPost,
                            onSubredditClick = navigator::navigateToSubreddit,
                            onUserClick = navigator::navigateToUser,
                            onImageClick = navigator::navigateToImage,
                            onSearchClick = navigator::navigateToSearch,
                            onVideoClick = navigator::navigateToVideo,
                            onFeedChange = { scope -> subredditScope = scope },
                            isTitleVisible = { visible -> isTitleVisible = visible },
                            modifier = modifier
                        )
                    }

                    TopLevelDestination.HOME -> {
                        HomeScreen(
                            onBackClick = { currentTopLevelDestination = TopLevelDestination.FEED },
                            onSubredditClick = navigator::navigateToSubreddit,
                            onSavedClick = navigator::navigateToBookmark,
                            onSearchClick = navigator::navigateToSearch,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}