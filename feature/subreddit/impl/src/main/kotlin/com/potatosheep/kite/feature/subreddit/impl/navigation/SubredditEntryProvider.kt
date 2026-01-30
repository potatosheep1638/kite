package com.potatosheep.kite.feature.subreddit.impl.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.subreddit.api.navigation.SubredditNavKey
import com.potatosheep.kite.feature.subreddit.impl.SubredditRoute
import com.potatosheep.kite.feature.subreddit.impl.SubredditViewModel
import com.potatosheep.kite.feature.subreddit.impl.SubredditViewModel.Factory
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<SubredditNavKey>.subredditEntry(navigator: Navigator) {
    entry<SubredditNavKey> { key ->
        SubredditRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onImageClick = navigator::navigateToImage,
            onSearchClick = navigator::navigateToSearch,
            onUserClick = navigator::navigateToUser,
            onVideoClick = navigator::navigateToVideo,
            viewModel = hiltViewModel<SubredditViewModel, Factory> {
                it.create(key.subreddit)
            }
        )
    }
}