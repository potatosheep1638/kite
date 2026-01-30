package com.potatosheep.kite.feature.search.impl.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.SearchNavKey
import com.potatosheep.kite.feature.search.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.search.impl.SearchRoute
import com.potatosheep.kite.feature.search.impl.SearchViewModel
import com.potatosheep.kite.feature.search.impl.SearchViewModel.Factory
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey> { key ->
        SearchRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onSubredditClick = navigator::navigateToSubreddit,
            onUserClick = navigator::navigateToUser,
            onImageClick = navigator::navigateToImage,
            onVideoClick = navigator::navigateToVideo,
            onSearchClick = navigator::navigateToSearch,
            viewModel = hiltViewModel<SearchViewModel, Factory> {
                it.create(key.subredditScope, key.sort, key.timeframe, key.query)
            }
        )
    }
}