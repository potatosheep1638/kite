package com.potatosheep.kite.app.nav

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.potatosheep.kite.app.ui.KiteAppState
import com.potatosheep.kite.feature.about.nav.aboutScreen
import com.potatosheep.kite.feature.bookmark.nav.bookmarkScreen
import com.potatosheep.kite.feature.image.nav.imageScreen
import com.potatosheep.kite.feature.image.nav.navigateToImage
import com.potatosheep.kite.feature.onboarding.nav.onboardingScreen
import com.potatosheep.kite.feature.post.nav.navigateToPost
import com.potatosheep.kite.feature.post.nav.postScreen
import com.potatosheep.kite.feature.search.nav.navigateToSearch
import com.potatosheep.kite.feature.search.nav.searchScreen
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

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        onboardingScreen(
            onBackClick = { navController.popBackStack() },
            onNextClick = { appState.navigateToTopLevelDestination(true) }
        )

        topLevelScreens(
            appState = appState,
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
            onSearchClick = navController::navigateToSearch,
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
            onSearchClick = navController::navigateToSearch,
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