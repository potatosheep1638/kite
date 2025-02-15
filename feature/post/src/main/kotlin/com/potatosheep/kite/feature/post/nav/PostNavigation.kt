package com.potatosheep.kite.feature.post.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import com.potatosheep.kite.feature.post.PostRoute
import kotlinx.serialization.Serializable

@Serializable
data class PostRoute(
    val subreddit: String,
    val postId: String,
    val commentId: String?,
    val thumbnailLink: String?,
    val isShareLink: Boolean,
    val findParents: Boolean,
)

fun NavController.navigateToPost(
    subreddit: String,
    postId: String,
    commentId: String?,
    thumbnailLink: String? = null,
    isShareLink: Boolean = false,
    findParents: Boolean = false,
    navOptions: NavOptions? = null
) = navigate(
    PostRoute(
        subreddit = subreddit,
        postId = postId,
        commentId = commentId,
        thumbnailLink = thumbnailLink,
        isShareLink = isShareLink,
        findParents = findParents
    ),
    navOptions
)

fun NavGraphBuilder.postScreen(
    onSubredditClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onImageClick: (List<String>, List<String?>) -> Unit,
    onMoreRepliesClick: (String, String, String?, String?, Boolean, Boolean) -> Unit,
    onFlairClick: (SortOption.Search, SortOption.Timeframe, String?, String) -> Unit,
    onVideoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable<PostRoute>(
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
        exitTransition = { fadeOut() + scaleOut() },
        popEnterTransition = { fadeIn() + scaleIn() },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) },
    ) {
        PostRoute(
            onSubredditClick = onSubredditClick,
            onUserClick = onUserClick,
            onImageClick = onImageClick,
            onMoreRepliesClick = onMoreRepliesClick,
            onFlairClick = onFlairClick,
            onVideoClick = onVideoClick,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}