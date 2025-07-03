package com.potatosheep.kite.feature.search.nav

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
import com.potatosheep.kite.feature.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable
data class Search(
    val subredditScope: String?,
    val sort: SortOption.Search,
    val timeframe: SortOption.Timeframe,
    val query: String
)

fun NavController.navigateToSearch(
    sort: SortOption.Search = SortOption.Search.RELEVANCE,
    timeframe: SortOption.Timeframe = SortOption.Timeframe.ALL,
    subredditScope: String? = null,
    query: String = "",
    navOptions: NavOptions? = null
) =
    navigate(
        route = Search(subredditScope, sort, timeframe, query),
        navOptions = navOptions
    )

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onPostClick: (String, String, String?, String?, Boolean) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onVideoClick: (String, String) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    modifier: Modifier = Modifier
) = composable<Search>(
    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down) },
    exitTransition = { fadeOut() + scaleOut() },
    popEnterTransition = { fadeIn() + scaleIn() },
    popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
) {
    SearchRoute(
        onBackClick = onBackClick,
        onPostClick = onPostClick,
        onSubredditClick = onSubredditClick,
        onUserClick = onUserClick,
        onImageClick = onImageClick,
        onVideoClick = onVideoClick,
        onSearchClick = onSearchClick,
        modifier = modifier,
    )
}