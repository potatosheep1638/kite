package com.potatosheep.kite.feature.searchresult.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.searchresult.api.navigation.SearchResultNavKey
import com.potatosheep.kite.feature.searchresult.api.navigation.navigateToSearchResult
import com.potatosheep.kite.feature.searchresult.impl.SearchResultRoute
import com.potatosheep.kite.feature.searchresult.impl.SearchResultViewModel
import com.potatosheep.kite.feature.searchresult.impl.SearchResultViewModel.Factory
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.searchResultEntry(navigator: Navigator) {
    entry<SearchResultNavKey>(metadata = defaultTransitionSpec()) { key ->
        SearchResultRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onSubredditClick = navigator::navigateToSubreddit,
            onUserClick = navigator::navigateToUser,
            onImageClick = navigator::navigateToImage,
            onVideoClick = navigator::navigateToVideo,
            onSearchClick = navigator::navigateToSearchResult,
            viewModel = hiltViewModel<SearchResultViewModel, Factory> {
                it.create(key.subredditScope, key.sort, key.timeframe, key.query)
            }
        )
    }
}