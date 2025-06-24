package com.potatosheep.kite.feature.library.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.feature.library.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) =
    navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<HomeRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        HomeRoute(
            onSubredditClick = onSubredditClick,
            onSavedClick = onSavedClick,
            modifier = modifier
        )
    }
}