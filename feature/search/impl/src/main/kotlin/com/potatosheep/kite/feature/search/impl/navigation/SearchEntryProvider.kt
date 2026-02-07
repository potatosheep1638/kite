package com.potatosheep.kite.feature.search.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.SearchNavKey
import com.potatosheep.kite.feature.search.impl.SearchRoute
import com.potatosheep.kite.feature.search.impl.SearchViewModel
import com.potatosheep.kite.feature.search.impl.SearchViewModel.Factory
import com.potatosheep.kite.feature.searchresult.api.navigation.navigateToSearchResult

fun EntryProviderScope<NavKey>.searchEntry(navigator: Navigator) {
    entry<SearchNavKey>(metadata = defaultTransitionSpec()) { key ->
        SearchRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onSearchClick = navigator::navigateToSearchResult,
            viewModel = hiltViewModel<SearchViewModel, Factory>() {
                it.create(key.subredditScope, key.sort, key.timeframe, key.query)
            }
        )
    }
}
