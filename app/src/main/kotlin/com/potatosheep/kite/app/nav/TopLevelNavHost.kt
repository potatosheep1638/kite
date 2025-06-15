package com.potatosheep.kite.app.nav

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import com.potatosheep.kite.app.ui.KiteAppState
import com.potatosheep.kite.core.designsystem.KiteNavigationBar
import com.potatosheep.kite.feature.about.nav.navigateToAbout
import com.potatosheep.kite.feature.bookmark.nav.navigateToBookmark
import com.potatosheep.kite.feature.feed.nav.feedScreen
import com.potatosheep.kite.feature.image.nav.navigateToImage
import com.potatosheep.kite.feature.library.nav.homeScreen
import com.potatosheep.kite.feature.post.nav.navigateToPost
import com.potatosheep.kite.feature.search.nav.navigateToSearch
import com.potatosheep.kite.feature.settings.nav.navigateToSettings
import com.potatosheep.kite.feature.subreddit.nav.navigateToSubreddit
import com.potatosheep.kite.feature.user.nav.navigateToUser
import com.potatosheep.kite.feature.video.nav.navigateToVideo
import kotlinx.serialization.Serializable

@Serializable
data object TopLevelRoute

@Composable
fun TopLevelNavHost(
    appState: KiteAppState,
    startDestination: Any,
    modifier: Modifier = Modifier
) {
    val topLevelNavController = appState.topLevelNavController
    val navController = appState.navController

    NavHost(
        navController = topLevelNavController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        feedScreen(
            onPostClick = navController::navigateToPost,
            onSubredditClick = navController::navigateToSubreddit,
            onUserClick = navController::navigateToUser,
            onImageClick = navController::navigateToImage,
            onSearchClick = navController::navigateToSearch,
            onVideoClick = navController::navigateToVideo,
            onSettingsClick = navController::navigateToSettings,
            onAboutClick = navController::navigateToAbout,
            navBar = {
                NavigationBar(
                    appState = appState,
                    modifier = modifier
                )
            },
            modifier = modifier
        )

        homeScreen(
            onSettingsClick = navController::navigateToSettings,
            onSearchClick = navController::navigateToSearch,
            onSubredditClick = navController::navigateToSubreddit,
            onSavedClick = navController::navigateToBookmark,
            onAboutClick = navController::navigateToAbout,
            navBar = {
                NavigationBar(
                    appState = appState,
                    modifier = modifier
                )
            },
            modifier = modifier
        )
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

@Composable
private fun NavigationBar(
    appState: KiteAppState,
    modifier: Modifier = Modifier
) {
    KiteNavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        val currentDestination = appState.currentTopLevelDestination

        appState.topLevelDestinations.forEach { destination ->
            val selected = currentDestination
                .isTopLevelDestinationInHierarchy(destination)

            NavigationBarItem(
                selected = selected,
                onClick = { appState.navigateToTopLevelDestination(destination) },
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
            )
        }
    }
}