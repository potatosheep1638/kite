package com.potatosheep.kite.feature.post.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.PostNavKey
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.post.impl.PostRoute
import com.potatosheep.kite.feature.post.impl.PostViewModel
import com.potatosheep.kite.feature.post.impl.PostViewModel.Factory
import com.potatosheep.kite.feature.searchresult.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.postEntry(navigator: Navigator) {
    entry<PostNavKey>(
        metadata = defaultTransitionSpec()
    ) { key ->
        PostRoute(
            onSubredditClick = navigator::navigateToSubreddit,
            onUserClick = navigator::navigateToUser,
            onImageClick = navigator::navigateToImage,
            onMoreRepliesClick = navigator::navigateToPost,
            onFlairClick = navigator::navigateToSearch,
            onVideoClick = navigator::navigateToVideo,
            onBackClick = { navigator.goBack() },
            viewModel = hiltViewModel<PostViewModel, Factory> {
                it.create(
                    subreddit = key.subreddit,
                    postId = key.postId,
                    commentId = key.commentId,
                    thumbnailLink = key.thumbnailLink,
                    isShareLink = key.isShareLink,
                    findParents = key.findParents
                )
            }
        )
    }
}