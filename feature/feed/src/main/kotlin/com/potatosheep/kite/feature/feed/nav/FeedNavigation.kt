package com.potatosheep.kite.feature.feed.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.potatosheep.kite.core.common.enums.SortOption
import com.potatosheep.kite.feature.feed.FeedRoute

@Composable
fun FeedScreen(
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