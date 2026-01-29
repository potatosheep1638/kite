package com.potatosheep.kite.feature.user.impl.nav

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
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.user.impl.UserRoute
import kotlinx.serialization.Serializable

@Serializable
data class UserNav(val user: String)

fun NavController.navigateToUser(user: String, navOptions: NavOptions? = null) =
    navigate(
        route = UserNav(user),
        navOptions = navOptions
    )

fun NavGraphBuilder.userScreen(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) = composable<UserNav>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },

) {
    UserRoute(
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onImageClick = onImageClick,
        onSearchClick = onSearchClick,
        onFlairClick = onFlairClick,
        onVideoClick = onVideoClick,
        modifier = modifier
    )
}