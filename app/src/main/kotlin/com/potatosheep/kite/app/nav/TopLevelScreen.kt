package com.potatosheep.kite.app.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.app.ui.KiteAppState
import com.potatosheep.kite.core.common.TopAppBarActionState
import com.potatosheep.kite.app.MenuOption
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.core.common.util.LocalSnackbarHostState
import com.potatosheep.kite.core.common.util.LocalTopAppBarActionState
import com.potatosheep.kite.core.common.util.alignToRightOfParent
import com.potatosheep.kite.core.designsystem.KiteDropdownMenu
import com.potatosheep.kite.core.designsystem.KiteDropdownMenuItem
import com.potatosheep.kite.core.designsystem.KiteIcons
import com.potatosheep.kite.core.designsystem.KiteNavigationSuiteScaffold
import com.potatosheep.kite.core.designsystem.KiteTopAppBar
import com.potatosheep.kite.core.designsystem.LocalBackgroundColor
import com.potatosheep.kite.core.translation.R.string as Translation
import com.potatosheep.kite.feature.about.impl.nav.navigateToAbout
import com.potatosheep.kite.feature.bookmark.impl.nav.navigateToBookmark
import com.potatosheep.kite.feature.feed.impl.nav.FeedScreen
import com.potatosheep.kite.feature.image.nav.navigateToImage
import com.potatosheep.kite.feature.home.impl.nav.HomeScreen
import com.potatosheep.kite.feature.post.impl.nav.navigateToPost
import com.potatosheep.kite.feature.search.nav.navigateToSearch
import com.potatosheep.kite.feature.settings.nav.navigateToSettings
import com.potatosheep.kite.feature.subreddit.nav.navigateToSubreddit
import com.potatosheep.kite.feature.user.nav.navigateToUser
import com.potatosheep.kite.feature.video.nav.navigateToVideo
import kotlinx.serialization.Serializable

@Serializable
data object TopLevelRoute

internal fun NavController.navigateToTopLevel(navOptions: NavOptions) =
    navigate(TopLevelRoute, navOptions)

internal fun NavGraphBuilder.topLevelScreens(
    appState: KiteAppState,
    modifier: Modifier
) {
    composable<TopLevelRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { ExitTransition.None }
    ) {
        val topAppBarActionState = remember { TopAppBarActionState() }

        CompositionLocalProvider(
            LocalTopAppBarActionState provides topAppBarActionState
        ) {
            TopLevelScreen(
                appState = appState,
                snackbarHostState = LocalSnackbarHostState.current,
                topAppBarActionState = LocalTopAppBarActionState.current,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopLevelScreen(
    appState: KiteAppState,
    snackbarHostState: SnackbarHostState,
    topAppBarActionState: TopAppBarActionState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController
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
            appState.topLevelDestinations.forEach { destination ->
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
                        navController.navigateToSearch(
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
                                                    navController.navigateToSettings()
                                                    isMenuExpanded = false
                                                }
                                            }

                                            MenuOption.ABOUT -> {
                                                {
                                                    navController.navigateToAbout()
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
                            onPostClick = navController::navigateToPost,
                            onSubredditClick = navController::navigateToSubreddit,
                            onUserClick = navController::navigateToUser,
                            onImageClick = navController::navigateToImage,
                            onSearchClick = navController::navigateToSearch,
                            onVideoClick = navController::navigateToVideo,
                            onFeedChange = { scope -> subredditScope = scope },
                            isTitleVisible = { visible -> isTitleVisible = visible },
                            modifier = modifier
                        )
                    }

                    TopLevelDestination.HOME -> {
                        HomeScreen(
                            onBackClick = { currentTopLevelDestination = TopLevelDestination.FEED },
                            onSubredditClick = navController::navigateToSubreddit,
                            onSavedClick = navController::navigateToBookmark,
                            onSearchClick = navController::navigateToSearch,
                            modifier = modifier
                        )
                    }
                }
            }
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false