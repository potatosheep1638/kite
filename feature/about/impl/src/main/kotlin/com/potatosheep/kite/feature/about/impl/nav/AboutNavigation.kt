package com.potatosheep.kite.feature.about.impl.nav

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
import com.potatosheep.kite.feature.about.impl.AboutRoute
import kotlinx.serialization.Serializable

@Serializable
data object AboutRoute

fun NavController.navigateToAbout(navOptions: NavOptions? = null) =
    navigate(
        AboutRoute,
        navOptions
    )

fun NavGraphBuilder.aboutScreen(
    versionName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) = composable<AboutRoute>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
) {
    AboutRoute(
        version = versionName,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
