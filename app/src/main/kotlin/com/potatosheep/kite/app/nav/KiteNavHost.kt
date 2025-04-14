package com.potatosheep.kite.app.nav

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import com.potatosheep.kite.app.ui.KiteAppState
import com.potatosheep.kite.core.designsystem.KiteFonts
import com.potatosheep.kite.core.designsystem.KiteNavigationBar
import com.potatosheep.kite.feature.about.nav.aboutScreen
import com.potatosheep.kite.feature.about.nav.navigateToAbout
import com.potatosheep.kite.feature.bookmark.nav.bookmarkScreen
import com.potatosheep.kite.feature.bookmark.nav.navigateToBookmark
import com.potatosheep.kite.feature.homefeed.nav.homeScreen
import com.potatosheep.kite.feature.image.nav.imageScreen
import com.potatosheep.kite.feature.image.nav.navigateToImage
import com.potatosheep.kite.feature.library.nav.libraryScreen
import com.potatosheep.kite.feature.onboarding.nav.onboardingScreen
import com.potatosheep.kite.feature.post.nav.navigateToPost
import com.potatosheep.kite.feature.post.nav.postScreen
import com.potatosheep.kite.feature.search.nav.navigateToSearch
import com.potatosheep.kite.feature.search.nav.searchScreen
import com.potatosheep.kite.feature.settings.nav.navigateToSettings
import com.potatosheep.kite.feature.settings.nav.settingsScreen
import com.potatosheep.kite.feature.subreddit.nav.navigateToSubreddit
import com.potatosheep.kite.feature.subreddit.nav.subredditScreen
import com.potatosheep.kite.feature.user.nav.navigateToUser
import com.potatosheep.kite.feature.user.nav.userScreen
import com.potatosheep.kite.feature.video.nav.navigateToVideo
import com.potatosheep.kite.feature.video.nav.videoScreen

@Composable
fun KiteNavHost(
    appState: KiteAppState,
    startDestination: Any,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val context = LocalContext.current

    // TODO: Try using NavigationSuiteScaffold + two NavHosts: one for top level and one for the rest
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingScreen(
            onBackClick = { navController.popBackStack() },
            onNextClick = { appState.navigateToTopLevelDestination(TopLevelDestination.HOME) }
        )

        homeScreen(
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

        libraryScreen(
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

        postScreen(
            onSubredditClick = navController::navigateToSubreddit,
            onUserClick = navController::navigateToUser,
            onImageClick = navController::navigateToImage,
            onMoreRepliesClick = navController::navigateToPost,
            onFlairClick = navController::navigateToSearch,
            onVideoClick = navController::navigateToVideo,
            onBackClick = { navController.popBackStack() },
            modifier = modifier
        )

        imageScreen(
            onBackClick = { navController.navigateUp() },
            modifier = modifier
        )

        subredditScreen(
            onBackClick = { navController.popBackStack() },
            onPostClick = navController::navigateToPost,
            onImageClick = navController::navigateToImage,
            onSearchClick = navController::navigateToSearch,
            onUserClick = navController::navigateToUser,
            onVideoClick = navController::navigateToVideo,
            modifier = modifier
        )

        userScreen(
            onBackClick = { navController.navigateUp() },
            onPostClick = navController::navigateToPost,
            onSubredditClick = navController::navigateToSubreddit,
            onImageClick = navController::navigateToImage,
            onFlairClick = navController::navigateToSearch,
            onVideoClick = navController::navigateToVideo,
            modifier = modifier
        )

        searchScreen(
            onBackClick = { navController.navigateUp() },
            onPostClick = navController::navigateToPost,
            onSubredditClick = navController::navigateToSubreddit,
            onUserClick = navController::navigateToUser,
            onImageClick = navController::navigateToImage,
            onVideoClick = navController::navigateToVideo,
            onSearchClick = navController::navigateToSearch,
            modifier = modifier
        )

        videoScreen(
            onBackClick = { navController.navigateUp() },
            modifier = modifier
        )

        settingsScreen(
            onBackClick = {
                navController.navigateUp()
            },
            modifier = modifier
        )

        bookmarkScreen(
            onBackClick = { navController.navigateUp() },
            onPostClick = navController::navigateToPost,
            onSubredditClick = navController::navigateToSubreddit,
            onUserClick = navController::navigateToUser,
            onImageClick = navController::navigateToImage,
            onVideoClick = navController::navigateToVideo,
            modifier = modifier
        )

        aboutScreen(
            versionName = getVersion(context),
            onBackClick = { navController.navigateUp() },
            modifier = modifier
        )
    }
}

@Composable
private fun NavigationBar(
    appState: KiteAppState,
    modifier: Modifier = Modifier
) {
    KiteNavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        val currentDestination = appState.currentDestination

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
                        fontFamily = KiteFonts.InterMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

private fun getVersion(context: Context): String {
    val packageManager = context.packageManager
    val packageName = context.packageName

    val packageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }

    return packageInfo.versionName ?: "null"
}