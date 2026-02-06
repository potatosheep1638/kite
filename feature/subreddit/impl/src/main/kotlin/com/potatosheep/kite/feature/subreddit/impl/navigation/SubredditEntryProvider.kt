package com.potatosheep.kite.feature.subreddit.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.searchresult.api.navigation.navigateToSearchResult
import com.potatosheep.kite.feature.subreddit.api.navigation.SubredditNavKey
import com.potatosheep.kite.feature.subreddit.impl.SubredditRoute
import com.potatosheep.kite.feature.subreddit.impl.SubredditViewModel
import com.potatosheep.kite.feature.subreddit.impl.SubredditViewModel.Factory
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.subredditEntry(navigator: Navigator) {
    entry<SubredditNavKey>(metadata = defaultTransitionSpec()) { key ->
        SubredditRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onImageClick = navigator::navigateToImage,
            onSearchClick = navigator::navigateToSearchResult,
            onUserClick = navigator::navigateToUser,
            onVideoClick = navigator::navigateToVideo,
            viewModel = hiltViewModel<SubredditViewModel, Factory> {
                it.create(key.subreddit)
            }
        )
    }
}