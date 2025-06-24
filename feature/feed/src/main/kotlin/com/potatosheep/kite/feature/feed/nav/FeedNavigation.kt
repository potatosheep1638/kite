package com.potatosheep.kite.feature.feed.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.feed.FeedRoute
import kotlinx.serialization.Serializable

@Serializable
data object FeedRoute

fun NavController.navigateToFeed(navOptions: NavOptions) = navigate(FeedRoute, navOptions)

fun NavGraphBuilder.feedScreen(
    onPostClick: (String, String, String?, String?) -> Unit,
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onFeedChange: (String?) -> Unit,
    isTitleVisible: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    composable<FeedRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        FeedRoute(
            onPostClick = onPostClick,
            onSubredditClick = onSubredditClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onSearchClick = onSearchClick,
            onVideoClick = onVideoClick,
            onFeedChange = onFeedChange,
            isTitleVisible = isTitleVisible,
            modifier = modifier,
        )
    }
}