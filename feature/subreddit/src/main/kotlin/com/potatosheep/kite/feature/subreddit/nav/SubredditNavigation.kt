package com.potatosheep.kite.feature.subreddit.nav

import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.subreddit.SubredditRoute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class Subreddit(
    val subreddit: String
)

fun NavController.navigateToSubreddit(
    subreddit: String,
    navOptions: NavOptions? = null
) = navigate(
    route = Subreddit(subreddit),
    navOptions = navOptions
)

fun NavGraphBuilder.subredditScreen(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onUserClick: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    composable<Subreddit>(
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
        deepLinks = listOf(
            navDeepLink<Subreddit>(
                basePath = "kite://kite-app/subreddit"
            )
        )
    ) {
        SubredditRoute(
            onBackClick = onBackClick,
            onPostClick = onPostClick,
            onImageClick = onImageClick,
            onSearchClick = onSearchClick,
            onUserClick = onUserClick,
            onVideoClick = onVideoClick,
            modifier = modifier
        )
    }
}