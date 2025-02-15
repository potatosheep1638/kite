package com.potatosheep.kite.feature.video.nav

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
import com.potatosheep.kite.feature.video.VideoRoute
import kotlinx.serialization.Serializable

@Serializable
data class VideoRoute(val videoLink: String)

fun NavController.navigateToVideo(videoLink: String, navOptions: NavOptions? = null) =
    navigate(
        route = VideoRoute(videoLink),
        navOptions = navOptions
    )

fun NavGraphBuilder.videoScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) = composable<VideoRoute>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) },
) {
    VideoRoute(
        onBackClick = onBackClick,
        modifier = modifier
    )
}
