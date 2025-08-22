package com.potatosheep.kite.feature.bookmark.nav

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
import com.potatosheep.kite.feature.bookmark.BookmarkRoute
import kotlinx.serialization.Serializable

@Serializable
data object BookmarkRoute

fun NavController.navigateToBookmark(navOptions: NavOptions? = null) =
    navigate(
        BookmarkRoute,
        navOptions
    )

fun NavGraphBuilder.bookmarkScreen(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?) -> Unit,
    onVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) = composable<BookmarkRoute>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
) {

    BookmarkRoute(
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onSearchClick = onSearchClick,
        onVideoClick = onVideoClick,
        modifier = modifier
    )
}