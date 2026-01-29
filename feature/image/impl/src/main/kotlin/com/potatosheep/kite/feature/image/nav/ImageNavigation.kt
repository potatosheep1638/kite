package com.potatosheep.kite.feature.image.nav

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
import androidx.navigation.navDeepLink
import com.potatosheep.kite.feature.image.ImageRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class Image(
    val params: ImageParameters
)

fun NavController.navigateToImage(
    imageLinks: List<String>,
    captions: List<String?>,
    navOptions: NavOptions? = null
) {
    navigate(
        route = Image(
            params = ImageParameters(
                imageLinks = imageLinks,
                captions = captions
            )
        ),
        navOptions = navOptions
    )
}

fun NavGraphBuilder.imageScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<Image>(
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) },
        typeMap = mapOf(typeOf<ImageParameters>() to ImageParametersType),
        deepLinks = listOf(
            navDeepLink<Image>(
                typeMap = mapOf(typeOf<ImageParameters>() to ImageParametersType),
                basePath = "kite://kite-app/image/",
            )
        )
    ) {
        ImageRoute(
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}