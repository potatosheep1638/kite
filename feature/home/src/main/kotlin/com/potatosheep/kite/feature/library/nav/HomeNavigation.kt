package com.potatosheep.kite.feature.library.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.library.HomeRoute

@Composable
fun HomeScreen(
    onBackClick: () -> Unit,
    onSubredditClick: (String) -> Unit,
    onSavedClick: () -> Unit,
    onSearchClick: (SortOption.Search, SortOption.Timeframe, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    HomeRoute(
        onBackClick = onBackClick,
        onSubredditClick = onSubredditClick,
        onSavedClick = onSavedClick,
        onSearchClick = onSearchClick,
        modifier = modifier
    )
}