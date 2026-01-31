package com.potatosheep.kite.feature.bookmark.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.bookmark.api.navigation.BookmarkNavKey
import com.potatosheep.kite.feature.bookmark.impl.BookmarkRoute
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.navigateToUser
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.bookmarkEntry(navigator: Navigator) {
    entry<BookmarkNavKey>(metadata = defaultTransitionSpec()) {
        BookmarkRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onSubredditClick = navigator::navigateToSubreddit,
            onUserClick = navigator::navigateToUser,
            onImageClick = navigator::navigateToImage,
            onSearchClick = navigator::navigateToSearch,
            onVideoClick = navigator::navigateToVideo,
        )
    }
}
