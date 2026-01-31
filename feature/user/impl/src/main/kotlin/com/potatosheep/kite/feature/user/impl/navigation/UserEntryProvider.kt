package com.potatosheep.kite.feature.user.impl.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.potatosheep.kite.core.designsystem.defaultTransitionSpec
import com.potatosheep.kite.core.navigation.Navigator
import com.potatosheep.kite.feature.image.api.navigation.navigateToImage
import com.potatosheep.kite.feature.post.api.navigation.navigateToPost
import com.potatosheep.kite.feature.search.api.navigation.navigateToSearch
import com.potatosheep.kite.feature.subreddit.api.navigation.navigateToSubreddit
import com.potatosheep.kite.feature.user.api.navigation.UserNavKey
import com.potatosheep.kite.feature.user.impl.UserRoute
import com.potatosheep.kite.feature.user.impl.UserViewModel
import com.potatosheep.kite.feature.user.impl.UserViewModel.Factory
import com.potatosheep.kite.feature.video.api.navigation.navigateToVideo

fun EntryProviderScope<NavKey>.userEntry(navigator: Navigator) {
    entry<UserNavKey>(metadata = defaultTransitionSpec()) { key ->
        UserRoute(
            onBackClick = { navigator.goBack() },
            onPostClick = navigator::navigateToPost,
            onSubredditClick = navigator::navigateToSubreddit,
            onImageClick = navigator::navigateToImage,
            onSearchClick = navigator::navigateToSearch,
            onFlairClick = navigator::navigateToSearch,
            onVideoClick = navigator::navigateToVideo,
            viewModel = hiltViewModel<UserViewModel, Factory> {
                it.create(key.user)
            }
        )
    }
}