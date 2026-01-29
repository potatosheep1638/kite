package com.potatosheep.kite.feature.settings.impl.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.feature.settings.impl.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(
        SettingsRoute,
        navOptions
    )

fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) = composable<SettingsRoute>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
) {
    SettingsRoute(
        onBackClick = onBackClick,
        modifier = modifier
    )
}