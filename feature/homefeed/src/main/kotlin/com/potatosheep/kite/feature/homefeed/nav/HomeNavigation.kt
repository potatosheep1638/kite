package com.potatosheep.kite.feature.homefeed.nav

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.homefeed.HomeFeedRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<HomeRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { ExitTransition.None }
    ) {
        HomeFeedRoute(
            onPostClick = onPostClick,
            onSubredditClick = onSubredditClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onSearchClick = onSearchClick,
            onVideoClick = onVideoClick,
            onSettingsClick = onSettingsClick,
            onAboutClick = onAboutClick,
            navBar = navBar,
            modifier = modifier
        )
    }
}