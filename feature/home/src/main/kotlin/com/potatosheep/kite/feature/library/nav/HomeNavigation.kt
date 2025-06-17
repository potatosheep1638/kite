package com.potatosheep.kite.feature.library.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.potatosheep.kite.feature.library.HomeRoute
import kotlinx.serialization.Serializable

const val LIBRARY_ROUTE = "LibraryRoute"

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) =
    navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    onSettingsClick: () -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<HomeRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { ExitTransition.None }
    ) {
        HomeRoute(
            onSettingsClick = onSettingsClick,
            onSearchClick = onSearchClick,
            onSubredditClick = onSubredditClick,
            onSavedClick = onSavedClick,
            onAboutClick = onAboutClick,
            modifier = modifier
        )
    }
}